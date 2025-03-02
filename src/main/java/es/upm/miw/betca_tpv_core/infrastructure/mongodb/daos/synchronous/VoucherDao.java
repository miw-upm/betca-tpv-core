package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoucherDao extends MongoRepository<Voucher, String> {
}
