package org.example.expert.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final TodoRepository todoRepository;

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
}
