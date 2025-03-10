package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ComplaintReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Log4j2
@Repository
public class ComplaintPersistenceMongodb implements ComplaintPersistence {

    private final ComplaintReactive complaintReactive;

    @Autowired
    public ComplaintPersistenceMongodb(ComplaintReactive complaintReactive){
        this.complaintReactive=complaintReactive;
    }

    @Override
    public Mono<Complaint> create(Complaint complaint) {
        return this.complaintReactive.create(complaint);
    }

    @Override
    public Flux<Complaint> findByUserMobileNullSafe(String userMobile) {
        return complaintReactive.findByUserMobileNullSafe(userMobile).map(ComplaintEntity::toComplaint);
    }

    @Override
    public Mono<Complaint> readById(String id) {
        return complaintReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non Existent Complaint id:"+id)))
                .map(ComplaintEntity::toComplaint);
    }
}
