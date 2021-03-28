package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VoucherPersistence {
    Mono<Voucher> create(Voucher voucher);
    Flux< Voucher > readAll();
    Mono<Voucher> readByReference(String reference);
    Mono<Voucher> consume(String reference);
}
