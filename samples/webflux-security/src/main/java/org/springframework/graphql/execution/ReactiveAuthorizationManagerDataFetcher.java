package org.springframework.graphql.execution;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class ReactiveAuthorizationManagerDataFetcher implements DataFetcher<Object> {

	private static final Authentication ANONYMOUS_AUTHENTICATION_TOKEN = new AnonymousAuthenticationToken("key", "anonymous",
			AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

	private final ReactiveAuthorizationManager<Field> authorizationManager;

	private final DataFetcher delegate;

	public ReactiveAuthorizationManagerDataFetcher(ReactiveAuthorizationManager<Field> authorizationManager, DataFetcher delegate) {
		this.authorizationManager = authorizationManager;
		this.delegate = delegate;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		Mono<Authentication> authentication = ReactiveSecurityContextHolder.getContext()
				.map(SecurityContext::getAuthentication)
				.defaultIfEmpty(ANONYMOUS_AUTHENTICATION_TOKEN);
		return this.authorizationManager.check(authentication, environment.getField())
				.filter(AuthorizationDecision::isGranted)
				.flatMap((authorization) -> doInvoke(this.delegate, environment));
	}

	private static Mono<Object> doInvoke(DataFetcher<?> delegate, DataFetchingEnvironment environment) {
		try {
			Object result = delegate.get(environment);
			// FIXME: Handle Flux and other Publishers?
			return (result instanceof Mono) ? (Mono<Object>) result : Mono.justOrEmpty(result);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
