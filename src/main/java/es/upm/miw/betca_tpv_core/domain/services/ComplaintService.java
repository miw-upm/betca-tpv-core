package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ComplaintService {
    private final ComplaintPersistence complaintPersistence;

    private final UserMicroservice userMicroservice;
    @Autowired
    public ComplaintService (ComplaintPersistence complaintPersistence,UserMicroservice userMicroservice){
        this.complaintPersistence=complaintPersistence;
        this.userMicroservice=userMicroservice;
    }

    public Flux<Complaint> findByUserMobileNullSafe(String userMobile){
        return this.complaintPersistence.findByUserMobileNullSafe(userMobile);
    }

    public Mono<Complaint> read(String id,String userLoggedMobile){
        return this.userMicroservice.readByMobile(userLoggedMobile)
                .flatMap(user -> {

                });

    }
}
