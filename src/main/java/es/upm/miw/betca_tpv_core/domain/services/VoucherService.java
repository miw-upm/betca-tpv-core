package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfVoucherBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        return this.verifyUserExistsByMobile(userMobile).then(this.voucherPersistence.create(voucher));
    }

    public Mono<Voucher> read(String reference) {
        return this.voucherPersistence.readByReference(reference);
    }

    public Mono<Voucher> update(String reference, Voucher voucher) {
        return this.voucherPersistence.update(reference, voucher);
    }

    private Mono<Void> verifyUserExistsByMobile(String userMobile) {
        return userMicroservice.readByMobile(userMobile)
                .onErrorResume(BadRequestException.class, Mono::error)
                .then();
    }

    public Flux<Voucher> findByReferenceAndValueNullSafe(String reference, BigDecimal value) {
        return this.voucherPersistence.findByReferenceAndValueNullSafe(reference, value);
    }

    public Flux<Voucher> findByDateRangeAndConsumedNullSafe(LocalDateTime startDate, LocalDateTime endDate, Boolean consumed){
        return this.voucherPersistence.findByDateRangeAndConsumedNullSafe(startDate, endDate, consumed);
    }

    public Mono<byte[]> readPdf(String reference) {
        return this.voucherPersistence.readByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Voucher not found with reference: " + reference)))
                .map(new PdfVoucherBuilder()::generateVoucher);

    }
}
