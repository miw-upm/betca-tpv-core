package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.VoucherReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class VoucherPersistenceMongodb implements VoucherPersistence {

    private final VoucherReactive voucherReactive;

    public VoucherPersistenceMongodb(VoucherReactive voucherReactive) {
        this.voucherReactive = voucherReactive;
    }

    @Override
    public Flux<Voucher> readAll() {
        return this.voucherReactive.findAll()
                .map(VoucherEntity::toVoucher);
    }
}
