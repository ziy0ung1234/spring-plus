package org.example.expert.domain.log.listener;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.event.ManagerRegisterSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ManagerRegisterSuccessEventListener {

    private final LogService logService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ManagerRegisterSuccessEvent event) {
        logService.save(
                Log.managerResister(
                        event.getRequesUser(),
                        event.getTargetUser(),
                        "매니저 등록 성공"
                )
        );
    }
}
