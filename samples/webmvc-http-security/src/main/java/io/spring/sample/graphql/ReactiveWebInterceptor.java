package io.spring.sample.graphql;

import org.springframework.graphql.web.WebGraphQlHandler;
import org.springframework.graphql.web.WebInput;
import org.springframework.graphql.web.WebInterceptor;
import org.springframework.graphql.web.WebOutput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ReactiveWebInterceptor implements WebInterceptor {
	@Override
	public Mono<WebOutput> intercept(WebInput webInput, WebGraphQlHandler next) {
		return Mono.delay(Duration.ofMillis(10)).flatMap((aLong) -> next.handle(webInput));
	}
}
