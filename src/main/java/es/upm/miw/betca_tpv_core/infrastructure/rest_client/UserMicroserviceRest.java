package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
                        .exchange())
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. User Microservice. " + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("User id: " + mobile));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("User id: " + mobile));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: User Microservice."));
                    } else {
                        return response.bodyToMono(User.class);
                    }
                });
    }
}
