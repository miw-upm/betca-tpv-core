package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class VoucherService {
    private final VoucherPersistence voucherPersistence;

    public VoucherService(VoucherPersistence voucherPersistence) {
        this.voucherPersistence = voucherPersistence;
    }

    public Flux< Voucher > readAll() {
        return this.voucherPersistence.readAll();
    }
}