package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.PrivilegedRoles;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
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

    private final ArticlePersistence articlePersistence;

    private final UserMicroservice userMicroservice;
    @Autowired
    public ComplaintService (ComplaintPersistence complaintPersistence,UserMicroservice userMicroservice,ArticlePersistence articlePersistence){
        this.complaintPersistence=complaintPersistence;
        this.userMicroservice=userMicroservice;
        this.articlePersistence = articlePersistence;
    }

    public Mono<Complaint> create(Complaint complaint,Authentication authentication){
        if(!complaint.getUserMobile().equals(authentication.getPrincipal())){
            return Mono.error(new ForbiddenException("You do not have permission to create a complaint for other user"));
        }
        return this.articlePersistence.readByBarcode(complaint.getBarcode())
                .switchIfEmpty(Mono.error(new NotFoundException("The article provided not exists")))
                .then(this.assertComplaintWithBarcodeAndUserMobileNotExists(complaint.getUserMobile(),complaint.getBarcode())
                                        .then(this.complaintPersistence.create(complaint)));

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

    private Mono<Void> assertComplaintWithBarcodeAndUserMobileNotExists(String userMobile,String barcode){
        return this.complaintPersistence.findByUserMobileAndBarcode(userMobile,barcode)
                .flatMap(complaint -> Mono.error(new ConflictException("There is already a complaint for the article and the user provided")));
    }
}
