package io.dltshv.todobox.service;

import io.dltshv.todobox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Primary
@RequiredArgsConstructor
public class MongoReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository repo;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
