package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.VoucherReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
    public Mono<Voucher> update(String reference, Voucher voucher) {
        if (voucher.getReference() != null && !voucher.getReference().equals(reference)) {
            voucher.setReference(reference);
        }

        return this.voucherReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent voucher reference: " + reference)))
                .flatMap(existingVoucherEntity -> {
                    BeanUtils.copyProperties(voucher, existingVoucherEntity);
                    return this.voucherReactive.save(existingVoucherEntity);
                })
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Flux<Voucher> findByReferenceAndValueNullSafe(String reference, BigDecimal value) {
        return this.voucherReactive.findByReferenceAndValueNullSafe(reference,value)
                .map(VoucherEntity::toVoucher);
    }

    @Override
    public Flux<Voucher> findVouchersWithFilters(LocalDateTime startDate, LocalDateTime endDate, Boolean consumed) {

        if (startDate != null && endDate != null) {
            if (consumed) {
                return voucherReactive.findConsumedVouchersByDateRange(startDate, endDate)
                        .map(VoucherEntity::toVoucher);
            } else {
                return voucherReactive.findNonConsumedVouchersByDateRange(startDate, endDate)
                        .map(VoucherEntity::toVoucher);
            }
        } else {
            if (consumed) {
                return voucherReactive.findConsumedVouchers()
                        .map(VoucherEntity::toVoucher);
            } else {
                return voucherReactive.findNonConsumedVouchers()
                        .map(VoucherEntity::toVoucher);
            }
        }
    }
}
