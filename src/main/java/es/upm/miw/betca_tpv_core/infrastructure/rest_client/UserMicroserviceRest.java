package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("userClient")
public class UserMicroserviceRest implements UserMicroservice {

    private final String userUri;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public UserMicroserviceRest(@Value("${miw.tpv.user}") String userUri, WebClient.Builder webClientBuilder) {
        this.userUri = userUri;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<User> readByMobile(String mobile) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getCredentials())
                .flatMap(token -> webClientBuilder.build()
                        .mutate().defaultHeader("Authorization", "Bearer " + token).build()
                        .get()
                        .uri(userUri + "/users/" + mobile)
                        .retrieve()
                        .bodyToMono(User.class)
                        .onErrorMap(Exception.class, exception ->
                                new BadGatewayException("Unexpected error: " + exception.getClass() + " - " + exception.getMessage()))
                );
    }
}
