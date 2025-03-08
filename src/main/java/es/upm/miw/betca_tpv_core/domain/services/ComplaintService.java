package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.PrivilegedRoles;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;


@Service
public class ComplaintService {
    private final ComplaintPersistence complaintPersistence;

    @Autowired
    public ComplaintService (ComplaintPersistence complaintPersistence,UserMicroservice userMicroservice){
        this.complaintPersistence=complaintPersistence;
    }

    public Mono<Complaint> read(String id, Authentication authentication) {

        System.out.println(authentication.getAuthorities());
        return this.complaintPersistence.read(id)
                .filter(complaint -> ( true
                        || complaint.getUserMobile().equals(authentication.getPrincipal()))
                )
                .switchIfEmpty(Mono.error(new ForbiddenException("You do not have permission to read this complaint")));
    }

    public Flux<Complaint> findByUserMobileNullSafe(String userMobile){
        return this.complaintPersistence.findByUserMobileNullSafe(userMobile);
    }
}
