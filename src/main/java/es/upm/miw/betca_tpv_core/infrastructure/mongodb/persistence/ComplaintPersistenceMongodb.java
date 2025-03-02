package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ComplaintReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ComplaintPersistenceMongodb implements ComplaintPersistence {

    private final ComplaintReactive complaintReactive;

    @Autowired
    public ComplaintPersistenceMongodb(ComplaintReactive complaintReactive){
        this.complaintReactive=complaintReactive;
    }
    @Override
    public Flux<Complaint> findByUserMobileNullSafe(String userMobile) {
        return complaintReactive.findByUserMobileNullSafe(userMobile).map(ComplaintEntity::toComplaint);
    }

    @Override
    public  Flux<Complaint> findByUserMobile(String userMobile){
        return  complaintReactive.findByUserMobile(userMobile).map(ComplaintEntity::toComplaint);
    }
}
