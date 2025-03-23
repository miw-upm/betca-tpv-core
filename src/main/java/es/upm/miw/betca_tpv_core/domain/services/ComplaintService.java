package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ComplaintService {
    private final ComplaintPersistence complaintPersistence;

    private final ArticlePersistence articlePersistence;

    @Autowired
    public ComplaintService (ComplaintPersistence complaintPersistence,ArticlePersistence articlePersistence){
        this.complaintPersistence=complaintPersistence;
        this.articlePersistence = articlePersistence;
    }

    public Mono<Complaint> create(Complaint complaint,Authentication authentication){
        if(!complaint.getUserMobile().equals(authentication.getPrincipal())){
            return Mono.error(new ForbiddenException("You do not have permission to create a complaint for other user"));
        }

        return this.articlePersistence.readByBarcode(complaint.getBarcode())
                .switchIfEmpty(Mono.error(new NotFoundException("The article provided not exists")))
                .then(this.assertComplaintWithBarcodeAndUserMobileWithOpenStateNotExists(
                        complaint.getUserMobile(), complaint.getBarcode()))
                .then(this.generateTrackingCode(complaint.getUserMobile(), complaint.getBarcode(), ComplaintState.OPEN.toString())
                        .map(trackingCode -> {
                            complaint.setTrackingCode(trackingCode);
                            return complaint;
                        }))
                .flatMap(this.complaintPersistence::create);


    }

    private Mono<String> generateTrackingCode(String userMobile, String barcode, String state) {
        return Mono.just(userMobile + "-" + barcode + "-" + state)
                .map(value -> {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] digest = md.digest(value.getBytes());
                        return new BigInteger(1, digest).toString(16).substring(0, 6).toUpperCase();
                    } catch (Exception e) {
                        throw new RuntimeException("Error generating hash", e);
                    }
                });
    }


    public Mono<Complaint> readByTrackingCode(String trackingCode, Authentication authentication) {

        Set<String> PRIVILEGED_ROLES = Arrays.stream(PrivilegedRoles.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        boolean hasPriviligedRoles= authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(PRIVILEGED_ROLES::contains);

        return this.complaintPersistence.readByTrackingCode(trackingCode)
                .switchIfEmpty(Mono.error(new NotFoundException("Non Existent Complaint trackingCode:"+trackingCode)))
                .filter(complaint -> ( hasPriviligedRoles
                        || complaint.getUserMobile().equals(authentication.getPrincipal()))
                )
                .switchIfEmpty(Mono.error(new ForbiddenException("You do not have permission to read this complaint")));
    }

    public Flux<Complaint> findByUserMobileNullSafe(String userMobile){
        return this.complaintPersistence.findByUserMobileNullSafe(userMobile);
    }

    public Mono<Void> delete(String trackingCode,Authentication authentication){
        Set<String> PRIVILEGED_ROLES = Arrays.stream(PrivilegedRoles.values())
                .filter(privilegedRoles -> privilegedRoles.equals(PrivilegedRoles.ROLE_ADMIN))
                .map(Enum::name)
                .collect(Collectors.toSet());

        boolean hasPriviligedRoles= authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(PRIVILEGED_ROLES::contains);

        return this.complaintPersistence.readByTrackingCode(trackingCode)
                .switchIfEmpty(Mono.empty())
                .flatMap(complaint -> {
                    if(!hasPriviligedRoles && !complaint.getUserMobile().equals(authentication.getPrincipal())){
                        return Mono.error(new ForbiddenException("You do not have permission to delete this complaint"));
                    }
                    if  (!hasPriviligedRoles &&  complaint.getState().equals(ComplaintState.CLOSED)){
                        return Mono.error(new ConflictException("You cannot delete a complaint with closed status"));
                    }
                    return this.complaintPersistence.delete(complaint);
                });
    }

    private Mono<Void> assertComplaintWithBarcodeAndUserMobileWithOpenStateNotExists(String userMobile,String barcode){
        return this.complaintPersistence.findByUserMobileAndBarcodeAndState(userMobile,barcode, ComplaintState.OPEN)
                .flatMap(complaint -> Mono.error(new ConflictException("There is already a complaint for the article and the user provided")));
    }
}
