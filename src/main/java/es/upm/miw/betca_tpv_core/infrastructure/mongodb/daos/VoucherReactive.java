package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface VoucherReactive extends ReactiveMongoRepository<VoucherEntity, String> {
    Mono<VoucherEntity> findByReference(String reference);
    Mono<VoucherEntity> findByUserMobile(String userMobile);
    @Query("{$and:["
            + "?#{ [0] == null ? {_id : {$ne:null}} : { reference : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { value : {$lt:[1]} } },"
            + "] }")
    Flux<VoucherEntity> findByReferenceAndValueNullSafe(
            String reference, BigDecimal value);
}
