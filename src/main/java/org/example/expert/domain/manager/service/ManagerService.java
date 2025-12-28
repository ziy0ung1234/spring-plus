package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.event.ManagerRegisterFailEvent;
import org.example.expert.domain.manager.event.ManagerRegisterSuccessEvent;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final ApplicationEventPublisher eventPublisher;

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
            targetUser = userRepository.findById(request.getManagerUserId())
                    .orElseThrow(() -> new InvalidRequestException("등록하려는 유저가 존재하지 않습니다."));

            Manager manager = Manager.create(requestUser, targetUser, todo);
            managerRepository.save(manager);
            //성공 로그
            eventPublisher.publishEvent(
                    new ManagerRegisterSuccessEvent(requestUser, targetUser)
            );

            return ManagerSaveResponse.from(manager);
        } catch (Exception e) {
            //실패 로그
            eventPublisher.publishEvent(
                    new ManagerRegisterFailEvent(requestUser, targetUser, e.getMessage())
            );
            throw e;
        }
    }

    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        return managerRepository.findByTodoIdWithUser(todo.getId())
                .stream()
                .map(ManagerResponse::from)
                .toList();
    }

    @Transactional
    public void deleteManager(Long userId, long todoId, long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("해당 유저가 존재하지 않습니다."));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        manager.validateDeletableManager(user,todo);
        managerRepository.delete(manager);
    }
}
