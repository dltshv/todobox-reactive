package io.dltshv.todobox;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoboxApplicationTests {

    @Autowired
    private TodoRepository repo;

	@Test
	public void canSave() {
        Mono<TodoEntity> sampleTodo = repo.save(new TodoEntity("sample todo description"));
        TodoEntity block = sampleTodo.block(Duration.ofDays(1L));
        assertThat(block).isNotNull();

        Flux<TodoEntity> todos = repo.findAll();
        assertThat(todos).isNotNull();
        assertThat(todos.count().block(Duration.ofDays(1L))).isGreaterThanOrEqualTo(1);
    }

}
