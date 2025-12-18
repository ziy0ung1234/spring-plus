package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerLogAspect {

    private final LogService logService;
    private final UserRepository userRepository;

    @AfterReturning("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void logManagerRegisterSuccess(JoinPoint joinPoint) {

        Long requesterId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User requestUser = userRepository.findById(requesterId).orElse(null);

        ManagerSaveRequest request =
                (ManagerSaveRequest) joinPoint.getArgs()[1];

        User targetUser = userRepository
                .findById(request.getManagerUserId())
                .orElse(null);

        logService.save(
                Log.managerResister(requestUser, targetUser, "매니저 등록 성공")
        );
    }

    @AfterThrowing(
            value = "execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))",
            throwing = "ex"
    )
    public void logManagerRegisterFail(JoinPoint joinPoint, Exception ex) {

        Long requesterId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User requestUser = userRepository.findById(requesterId).orElse(null);

        ManagerSaveRequest request =
                (ManagerSaveRequest) joinPoint.getArgs()[1];

        User targetUser = userRepository
                .findById(request.getManagerUserId())
                .orElse(null);

        logService.save(
                Log.managerResister(
                        requestUser,
                        targetUser,
                        "매니저 등록 실패: " + ex.getMessage()
                )
        );
    }

}
