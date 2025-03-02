package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface VoucherReactive extends ReactiveMongoRepository<VoucherEntity, String> {
    Mono<VoucherEntity> findByReference(String reference);
    Mono<VoucherEntity> findByUserMobile(String userMobile);
}
