package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.VoucherReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public class VoucherPersistenceMongodb implements VoucherPersistence {

    private final VoucherReactive voucherReactive;

    @Autowired
    public VoucherPersistenceMongodb(VoucherReactive voucherReactive) {
        this.voucherReactive = voucherReactive;
    }

    @Override
    public Mono<Voucher> create(Voucher voucher) {
        return this.voucherReactive.save(new VoucherEntity(voucher))
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Mono<Voucher> readByReference(String reference) {
        return this.voucherReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent voucher reference: " + reference)))
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Flux<Voucher> findByReferenceAndValueNullSafe(String reference, BigDecimal value) {
        return this.voucherReactive.findByReferenceAndValueNullSafe(reference,value)
                .map(VoucherEntity::toVoucher);
    }
}
