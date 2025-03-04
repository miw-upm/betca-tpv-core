package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class VoucherService {

    private final VoucherPersistence voucherPersistence;
    private final UserMicroservice userMicroservice;

    @Autowired
    public VoucherService(VoucherPersistence voucherPersistence, UserMicroservice userMicroservice) {
        this.voucherPersistence = voucherPersistence;
        this.userMicroservice = userMicroservice;
    }

    public Mono<Voucher> create(Voucher voucher) {
        String userMobile = voucher.getUser().getMobile();
        return this.voucherPersistence.create(voucher);
    }

    public Mono<Voucher> read(String reference) {
        return this.voucherPersistence.readByReference(reference);
    }

    private Mono<Void> verifyUserExistsByMobile(String userMobile) {
        return userMicroservice.readByMobile(userMobile)
                .onErrorResume(BadRequestException.class, Mono::error)
                .then();
    }

    public Flux<Voucher> findByReferenceAndValueNullSafe(String reference, BigDecimal value) {
        return this.voucherPersistence.findByReferenceAndValueNullSafe(reference, value);
    }
}
