package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VoucherService {

    private final VoucherPersistence voucherPersistence;

    @Autowired
    public VoucherService(VoucherPersistence voucherPersistence) {
        this.voucherPersistence = voucherPersistence;
    }

    public Mono<Voucher> create(Voucher voucher) {
        return this.voucherPersistence.create(voucher);
    }

    public Mono<Voucher> read(String reference) {
        return this.voucherPersistence.readByReference(reference);
    }
}
