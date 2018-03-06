package io.dltshv.todobox.repository;

import io.dltshv.todobox.entity.TodoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TodoRepository extends ReactiveCrudRepository<TodoEntity, String> {
}
