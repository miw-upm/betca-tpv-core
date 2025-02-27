package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ComplaintService {
    private final ComplaintPersistence complaintPersistence;
    @Autowired

    public ComplaintService (ComplaintPersistence complaintPersistence){
        this.complaintPersistence=complaintPersistence;
    }

    public Flux<Complaint> findByUserMobileNullSafe(String userMobile){
        return this.complaintPersistence.findByUserMobileNullSafe(userMobile);
    }

}
