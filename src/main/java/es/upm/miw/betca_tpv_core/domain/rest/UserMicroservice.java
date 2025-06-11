package es.upm.miw.betca_tpv_core.domain.rest;

import es.upm.miw.betca_tpv_core.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserMicroservice {

    Mono<User> readByMobile(String mobile);

    Mono<User> readByMobileWithAuthenticate(String mobile, String authenticate);

    Flux<User> findUsersNotInList(List<String> mobiles);

    Mono<String> test ();
}
