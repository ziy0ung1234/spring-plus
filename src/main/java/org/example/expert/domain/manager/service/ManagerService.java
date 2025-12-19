package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final LogService logService;

    @Transactional
    public ManagerSaveResponse saveManager(
            Long userId,
            long todoId,
            ManagerSaveRequest request
    ) {
        User requestUser = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("해당 유저가 존재하지 않습니다."));
        User targetUser = null;
        try {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() ->
                            new InvalidRequestException("Todo not found"));

            if (!todo.getUser().getId().equals(requestUser.getId())) {
                throw new InvalidRequestException("일정을 만든 사용자만 담당자를 등록할 수 있습니다.");
            }

            targetUser = userRepository.findById(request.getManagerUserId())
                    .orElseThrow(() -> new InvalidRequestException("등록하려는 유저가 존재하지 않습니다."));

            if (requestUser.getId().equals(targetUser.getId())) {
                throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
            }
            //로그 저장
            logService.save(Log.managerResister(requestUser, targetUser, "매니저 등록 성공"));

            Manager manager = managerRepository.save(new Manager(targetUser, todo));

            return new ManagerSaveResponse(
                    manager.getId(),
                    new UserResponse(targetUser.getId(), targetUser.getEmail())
            );
        } catch (Exception e) {
            logService.save(Log.managerResister(requestUser, targetUser, "매니저 등록 실패 : " + e.getMessage()));
            throw e;
        }
    }

    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    @Transactional
    public void deleteManager(Long userId, long todoId, long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("해당 유저가 존재하지 않습니다."));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.");
        }

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
        }

        managerRepository.delete(manager);
    }
}
