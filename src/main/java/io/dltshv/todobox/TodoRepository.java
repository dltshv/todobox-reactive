package io.dltshv.todobox;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TodoRepository extends ReactiveCrudRepository<TodoEntity, String> {
}
