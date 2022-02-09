package org.springframework.graphql.execution;

import graphql.language.Field;
import graphql.language.StringValue;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;

import java.util.function.Function;

public class AuthoritySchemaDirectiveWiring implements SchemaDirectiveWiring {

	private final String directiveName = "auth";

	private final String directiveArgumentName;

	private final Function<String, AuthorityReactiveAuthorizationManager<Field>> createAuthorizationManager;

	private AuthoritySchemaDirectiveWiring(String directiveArgumentName, Function<String, AuthorityReactiveAuthorizationManager<Field>> createAuthorizationManager) {
		this.directiveArgumentName = directiveArgumentName;
		this.createAuthorizationManager = createAuthorizationManager;
	}

	public static AuthoritySchemaDirectiveWiring hasAuthority() {
		return new AuthoritySchemaDirectiveWiring("authority", AuthorityReactiveAuthorizationManager::hasAuthority);
	}

	public static AuthoritySchemaDirectiveWiring hasRole() {
		return new AuthoritySchemaDirectiveWiring("role", AuthorityReactiveAuthorizationManager::hasRole);
	}

	@Override
	public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
		GraphQLFieldDefinition field = environment.getElement();
		GraphQLDirective directive = environment.getDirective(this.directiveName);

		if (directive == null) {
			return field;
		}

		GraphQLFieldsContainer parentType = environment.getFieldsContainer();
		DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);


		GraphQLArgument directiveArgument = directive.getArgument(this.directiveArgumentName);

		if (directiveArgument == null) {
			return field;
		}

		StringValue directiveArgumentValue = (StringValue) directiveArgument.getArgumentValue().getValue();
		AuthorityReactiveAuthorizationManager<Field> authorizationManager = this.createAuthorizationManager.apply(directiveArgumentValue.getValue());

		// now change the field definition to have the new authorising data fetcher
		environment.getCodeRegistry().dataFetcher(parentType, field, new ReactiveAuthorizationManagerDataFetcher(authorizationManager, originalDataFetcher));
		return field;
	}

}
