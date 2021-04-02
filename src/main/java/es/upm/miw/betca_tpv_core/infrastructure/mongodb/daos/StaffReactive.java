package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface StaffReactive extends ReactiveSortingRepository<LoginEntity, String> {
    Mono<LoginEntity> findTopByPhoneOrderByLoginDateDesc(String phone);
    Flux<LoginEntity> findByLoginDateBetweenAndPhone(LocalDateTime startDate, LocalDateTime endDate, String phone);
    Flux<LoginEntity> findByLoginDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
