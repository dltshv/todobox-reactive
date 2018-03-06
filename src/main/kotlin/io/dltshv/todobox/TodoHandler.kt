package io.dltshv.todobox

import io.dltshv.todobox.entity.TodoEntity
import io.dltshv.todobox.repository.TodoRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.BodyInserters.fromPublisher
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Service
class TodoHandler(val repo: TodoRepository) {

    fun all(request: ServerRequest): Mono<ServerResponse> = ok().body(fromPublisher(repo.findAll(), TodoEntity::class.java))

    fun getById(request: ServerRequest): Mono<ServerResponse> =
            ok().body(fromPublisher(repo.findById(request.pathVariable("id")), TodoEntity::class.java))

    fun deleteById(request: ServerRequest): Mono<ServerResponse> {
        repo.deleteById(request.pathVariable("id"))
        return ok().build()
    }

    fun add(request: ServerRequest): Mono<ServerResponse> {
        val savedTodo = request.bodyToMono(TodoEntity::class.java).flatMap { repo.save(it) }
        return ok().body(fromPublisher(savedTodo, TodoEntity::class.java))
    }

    fun hello(request: ServerRequest): Mono<ServerResponse> =
            request
                    .principal()
                    .map { it.name }
                    .flatMap { name ->
                        ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromObject(mapOf("message" to "Hello, $name!")))
                    }
}