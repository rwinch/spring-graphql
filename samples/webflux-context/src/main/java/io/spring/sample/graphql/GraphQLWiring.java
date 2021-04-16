package io.spring.sample.graphql;

import graphql.schema.idl.RuntimeWiring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.boot.RuntimeWiringCustomizer;
import org.springframework.stereotype.Component;

@Component
public class GraphQLWiring implements RuntimeWiringCustomizer {

    @Autowired
    DataFetchers dataFetchers;

    @Override
    public void customize(RuntimeWiring.Builder builder) {
        builder.type("Query",
                wiringBuilder -> {
                    return wiringBuilder.
                            dataFetcher("user", dataFetchers.userServiceFetcher);
                });

    }
}
