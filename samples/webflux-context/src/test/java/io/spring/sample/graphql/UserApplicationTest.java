package io.spring.sample.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;

import java.util.Collections;

@SpringBootTest()
class UserApplicationTest {

    @Autowired
    private ReactiveWebApplicationContext context;
    private static final String BASE_URL = "https://spring.example.org/graphql";


    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToApplicationContext(this.context)
                .apply(SecurityMockServerConfigurers.springSecurity())
                .configureClient()
                .filter(ExchangeFilterFunctions.basicAuthentication())
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .baseUrl(BASE_URL)
                .build();
    }

    @Test
    void canQueryName() {
        String query = "{" +
                "  user { " +
                "    username" +
                "  }" +
                "}";


        client.post().uri("")
                .bodyValue("{  \"query\": \"" + query + "\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("data.user.username").isEqualTo("rwinch");

    }
}