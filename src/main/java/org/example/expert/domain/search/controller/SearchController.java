package org.example.expert.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.search.service.SearchService;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodos(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String nickname,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort="createAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(
                searchService.search(
                        query,
                        nickname,
                        startDate,
                        endDate,
                        pageable
                )
        );
    }

    @GetMapping("users/search")
    public ResponseEntity<UserSearchResponse> searchUserNickname(
            @RequestParam String nickname
    ) {
        return ResponseEntity.ok(searchService.searchUser(nickname));
    }

}
