package io.spring.sample.graphql;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataFetchers {

    @Autowired
    UserService userService;

    public DataFetcher userServiceFetcher = environment -> {
        return userService.findCurrentUser();
    };

}
