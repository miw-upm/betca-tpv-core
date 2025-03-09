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
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ComplaintService {
    private final ComplaintPersistence complaintPersistence;


    @Autowired
    public ComplaintService (ComplaintPersistence complaintPersistence,UserMicroservice userMicroservice){
        this.complaintPersistence=complaintPersistence;
    }

    public Mono<Complaint> readById(String id, Authentication authentication) {

        Set<String> PRIVILEGED_ROLES = Arrays.stream(PrivilegedRoles.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        boolean hasPriviligedRoles= authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(PRIVILEGED_ROLES::contains);

        return this.complaintPersistence.readById(id)
                .filter(complaint -> ( hasPriviligedRoles
                        || complaint.getUserMobile().equals(authentication.getPrincipal()))
                )
                .switchIfEmpty(Mono.error(new ForbiddenException("You do not have permission to read this complaint")));
    }

    public Flux<Complaint> findByUserMobileNullSafe(String userMobile){
        return this.complaintPersistence.findByUserMobileNullSafe(userMobile);
    }

}
