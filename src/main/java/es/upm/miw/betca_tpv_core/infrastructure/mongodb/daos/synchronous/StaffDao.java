package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface StaffDao extends MongoRepository<LoginEntity, String> {
    Stream<LoginEntity> findByLoginDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
