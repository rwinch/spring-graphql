package io.spring.sample.graphql;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class UserService {

    public Mono<User> findCurrentUser() {
        return Mono.just(new User("rwinch"));
    }

}
