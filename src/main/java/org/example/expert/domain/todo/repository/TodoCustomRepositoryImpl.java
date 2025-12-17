package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user,user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(result);

    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String query,
            String nickname,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        List<TodoSearchResponse> result = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        manager.count(),
                        comment.count()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleCondition(query),
                        nicknameCondition(nickname),
                        startDateCondition(startDate),
                        endDateCondition(endDate)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        titleCondition(query),
                        nicknameCondition(nickname),
                        startDateCondition(startDate),
                        endDateCondition(endDate)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, total == null? 0:total);
    }

    // ------ BooleanExpression ------
    private BooleanExpression titleCondition(String query) {
        return isNotEmpty(query) ? todo.title.contains(query) : null;
    }
    private BooleanExpression nicknameCondition(String nickname) {
        return isNotEmpty(nickname) ? user.nickname.contains(nickname) : null;
    }
    private BooleanExpression startDateCondition(LocalDateTime startDate) {
        return startDate != null ? todo.createdAt.goe(startDate) : null;
    }
    private BooleanExpression endDateCondition(LocalDateTime endDate) {
        return endDate != null ? todo.createdAt.loe(endDate) : null;
    }


    private boolean isNotEmpty(String query) {
        return query != null && !query.isEmpty();
    }
}
