package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.VoucherReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class VoucherPersistenceMongodb implements VoucherPersistence {

    private final VoucherReactive voucherReactive;

    public VoucherPersistenceMongodb(VoucherReactive voucherReactive) {
        this.voucherReactive = voucherReactive;
    }

    @Override
    public Mono<Voucher> create(Voucher voucher) {
        VoucherEntity voucherEntity = new VoucherEntity(voucher);
        return voucherReactive.save(voucherEntity)
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Flux<Voucher> readAll() {
        return this.voucherReactive.findAll()
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Mono<Voucher> readByReference(String reference) {
        return this.voucherReactive.findById(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent voucher with reference: " + reference)))
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Mono<Voucher> consume(String reference) {
        return this.voucherReactive.findById(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent voucher with reference: " + reference)))
                .map(voucherEntity -> {
                    voucherEntity.setDateOfUse(LocalDateTime.now());
                    return voucherEntity;
                })
                .flatMap(this.voucherReactive::save)
                .map(VoucherEntity::toVoucher);
    }
}
