package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.persistence.VoucherPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfVoucherBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class VoucherService {
    private final VoucherPersistence voucherPersistence;

    public VoucherService(VoucherPersistence voucherPersistence) {
        this.voucherPersistence = voucherPersistence;
    }

    public Flux< Voucher > readAll() {
        return this.voucherPersistence.readAll();
    }

    public Mono< Voucher > readByReference(String reference) {
        return this.voucherPersistence.readByReference(reference);
    }

    public Mono< Voucher > create(Voucher voucher) {
        voucher.setReference(UUID.randomUUID());
        voucher.setCreationDate(LocalDateTime.now());
        return voucherPersistence.create(voucher);
    }

    public Mono<Voucher> consume(String reference) {
        return this.voucherPersistence.consume(reference);
    }

    public Mono<byte[]> printByReference(String reference) {
        return this.voucherPersistence.readByReference(reference)
                .map(new PdfVoucherBuilder()::generateVoucherTicket);
    }

    public Flux<Voucher> getUnconsumedVouchersBetweenDates(LocalDateTime from, LocalDateTime to) {
        from = from.toLocalDate().atTime(LocalTime.MIN);
        to = to.toLocalDate().atTime(LocalTime.MAX);
        return this.voucherPersistence.getUnconsumedVouchersBetweenDates(from, to);
    }
}
