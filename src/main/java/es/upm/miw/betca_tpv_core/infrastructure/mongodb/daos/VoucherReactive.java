package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

public interface VoucherReactive extends ReactiveSortingRepository<VoucherEntity, String> {
    Flux<VoucherEntity> findByCreationDateBetweenAndDateOfUseIsNull(LocalDateTime from, LocalDateTime to);
}
