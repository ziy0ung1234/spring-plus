package org.example.expert.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<TodoSearchResponse> search(
            String keyword,
            String managerNickname,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return todoRepository.searchTodos(
                keyword,
                managerNickname,
                startDate,
                endDate,
                pageable
        );
    }
    @Transactional(readOnly = true)
    @Cacheable(
            value = "userCache",
            key = "#nickname",
            unless = "#result == null"
    )
    public Optional<UserSearchResponse> searchUser(String nickname) {
        log.info("캐시에 없으니 DB에서 직접 조회");
        return userRepository.findExactNickname(nickname);
        }
}
