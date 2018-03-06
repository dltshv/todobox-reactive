package io.dltshv.todobox

import io.dltshv.todobox.repository.TodoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.router

@Configuration
@EnableWebFlux
class ControllerConfigurationKt {

    @Bean
    fun routerFunction(repo: TodoRepository, todoHandler: TodoHandler) =
            router {
                GET("/", todoHandler::all)
                GET("/{id}", todoHandler::getById)
                DELETE("/{id}", todoHandler::deleteById)
                POST("/add", todoHandler::add)
                GET("/hello", todoHandler::hello)
            }

}