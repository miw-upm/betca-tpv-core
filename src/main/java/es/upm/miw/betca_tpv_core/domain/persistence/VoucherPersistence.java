package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface VoucherPersistence {

    Mono<Voucher> create(Voucher voucher);

    Mono<Voucher> readByReference(String reference);

    Flux<Voucher> findByReferenceAndValueNullSafe(String reference, BigDecimal value);
}
