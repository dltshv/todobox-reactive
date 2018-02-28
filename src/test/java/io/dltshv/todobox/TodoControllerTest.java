package io.dltshv.todobox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoControllerTest {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private TodoRepository repo;

    private WebTestClient client;

    @Before
    public void setUp() {
        client = WebTestClient.bindToApplicationContext(ctx).build();
        repo.deleteAll();
        repo.saveAll(
                Arrays.asList(
                        new TodoEntity("test1"),
                        new TodoEntity("test2"),
                        new TodoEntity("test3")
                        )
        );
    }

    @Test
    public void canGet() {
        client.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoEntity.class);
    }

    @Test
    public void canAdd() {
        EntityExchangeResult<TodoEntity> result = client.post()
                .uri("/add")
                .body(Mono.just(new TodoEntity("test description")), TodoEntity.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoEntity.class)
                .returnResult();

        TodoEntity todoEntity = result.getResponseBody();
        assertThat(todoEntity)
                .isNotNull()
                .matches(e -> "test description".equals(e.getDescription()));
    }

    @Test
    public void canGetById() {
        Mono<TodoEntity> previouslyAddedTodo = repo.save(new TodoEntity("previously added todo"));
        TodoEntity todoEntity = previouslyAddedTodo.block();

        EntityExchangeResult<TodoEntity> exchangeResult = client.get()
                .uri("/" + todoEntity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoEntity.class)
                .returnResult();
        assertThat(exchangeResult.getResponseBody())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", todoEntity.getId());
    }

    @Test
    public void canDeleteById() {
        Mono<TodoEntity> previouslyAddedTodo = repo.save(new TodoEntity("previously added todo"));
        TodoEntity todoEntity = previouslyAddedTodo.block();

        client.delete()
                .uri("/" + todoEntity.getId())
                .exchange()
                .expectStatus().isOk();

        StepVerifier.create(repo.findById(todoEntity.getId()))
                .expectNoEvent(Duration.ZERO);
    }

}
