package org.example.expert.domain.log.listener;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.event.ManagerRegisterFailEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ManagerRegisterFailEventListener {

    private final LogService logService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handle(ManagerRegisterFailEvent event) {
        logService.save(
                Log.managerResister(
                        event.getRequestUser(),
                        event.getTargetUser(),
                        "매니저 등록 실패 : " + event.getReason()
                )
        );
    }
}
