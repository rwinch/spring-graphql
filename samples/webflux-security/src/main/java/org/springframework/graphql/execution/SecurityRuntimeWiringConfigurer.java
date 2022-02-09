package org.springframework.graphql.execution;

import graphql.schema.idl.RuntimeWiring;


public class SecurityRuntimeWiringConfigurer implements RuntimeWiringConfigurer {

	@Override
	public void configure(RuntimeWiring.Builder builder) {
		builder.directiveWiring(AuthoritySchemaDirectiveWiring.hasRole());
		builder.directiveWiring(AuthoritySchemaDirectiveWiring.hasAuthority());
	}
}
