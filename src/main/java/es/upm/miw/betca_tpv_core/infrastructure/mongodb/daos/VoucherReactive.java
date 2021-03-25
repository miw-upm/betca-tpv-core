package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.VoucherEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import java.util.UUID;

public interface VoucherReactive extends ReactiveSortingRepository<VoucherEntity, UUID> { }
