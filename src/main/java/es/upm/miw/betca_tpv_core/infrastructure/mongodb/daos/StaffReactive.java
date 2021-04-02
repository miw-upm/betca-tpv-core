package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface StaffReactive extends ReactiveSortingRepository<LoginEntity, String> {
    Mono<LoginEntity> findTopByPhoneOrderByLoginDateDesc(String phone);
}
