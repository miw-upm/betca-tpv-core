package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service("userClient")
@Log
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

    @Override
    public Mono<User> readByMobileWithAuthenticate(String mobile, String authenticate) {
        String finalUri = userUri + "/users/" + mobile;

        return  webClientBuilder
                .build()
                .get()
                .uri(finalUri)
                .header(HttpHeaders.AUTHORIZATION,
                         authenticate)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.createException()
                                .flatMap(Mono::error))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> clientResponse.createException()
                                .flatMap(Mono::error))
                .bodyToMono(User.class);
    }

    @Override
    public Flux<User> findUsersNotInList(List<String> userMobiles) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getCredentials().toString())
                .flatMapMany(token -> webClientBuilder.build()
                            .mutate().defaultHeader("Authorization", "Bearer " + token).build()
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(userUri + "/users" + "/not-in-list")
                                    .queryParam("userMobiles", String.join(",", userMobiles))
                                    .build())
                            .retrieve()
                            .bodyToFlux(User.class)
                            .onErrorMap(Exception.class, exception ->
                                    new BadGatewayException("Unexpected error: " + exception.getClass() + " - " + exception.getMessage()))
                );
    }

    @Override
    public Mono<String> test() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getCredentials().toString())
                .flatMap(token -> webClientBuilder.build()
                        .mutate().defaultHeader("Authorization", "Bearer " + token).build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(userUri + "/users" + "/not-in-list")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorMap(Exception.class, exception ->
                                new BadGatewayException("Unexpected error: " + exception.getClass() + " - " + exception.getMessage()))
                );
    }
}
