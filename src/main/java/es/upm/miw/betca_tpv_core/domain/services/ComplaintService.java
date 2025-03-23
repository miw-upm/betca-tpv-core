package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
import es.upm.miw.betca_tpv_core.domain.model.PrivilegedRoles;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintUpdateAdminDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;
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
                .then(this.assertComplaintWithBarcodeAndUserMobileAndStateNotExists(
                        complaint.getUserMobile(), complaint.getBarcode(),ComplaintState.OPEN))
                .then(this.generateTrackingCode(complaint.getUserMobile(), complaint.getBarcode(), ComplaintState.OPEN.toString())
                        .map(trackingCode -> {
                            complaint.setTrackingCode(trackingCode);
                            return complaint;
                        }))
                .flatMap(this.complaintPersistence::create);


    }

    private Mono<String> generateTrackingCode(String userMobile, String barcode, String state) {
        return Mono.just(userMobile + "-" + barcode + "-" + state + LocalDateTime.now().toString())
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

    public Mono<Complaint> updateAsAdmin(String trackingCode, ComplaintUpdateAdminDto complaintUpdateAdminDto){
        return this.complaintPersistence.readByTrackingCode(trackingCode)
                .switchIfEmpty(Mono.error(new NotFoundException("Non Existent Complaint trackingCode:"+trackingCode)))
                .flatMap(complaint -> {
                    boolean isModifiedBarcode = this.isModifiedString(complaintUpdateAdminDto.getBarcode(),complaint.getBarcode()),
                            isModifiedUserMobile = this.isModifiedString(complaintUpdateAdminDto.getUserMobile(),complaint.getUserMobile()),
                            isModifiedState = this.isModifiedComplaintState(complaintUpdateAdminDto.getState());

                    if (isModifiedBarcode) {
                        complaint.setBarcode(complaintUpdateAdminDto.getBarcode());
                    }
                    if (isModifiedUserMobile) {
                        complaint.setUserMobile(complaintUpdateAdminDto.getUserMobile());
                    }
                    complaint.setState(isModifiedState ? ComplaintState.OPEN : ComplaintState.CLOSED);

                    if (isModifiedBarcode || isModifiedUserMobile || isModifiedState) {
                        return assertComplaintWithBarcodeAndUserMobileAndStateNotExists(complaint.getUserMobile(),
                                complaint.getBarcode(),complaint.getState())
                                .then(
                                    Mono.just(complaint)
                                );
                    } else {
                        complaint.setReply(complaintUpdateAdminDto.getReply());
                        complaint.setDescription(complaintUpdateAdminDto.getDescription());
                        return Mono.just(complaint);
                    }
                })
                .flatMap( complaint ->
                        this.generateTrackingCode(complaint.getUserMobile(), complaint.getBarcode(), ComplaintState.OPEN.toString())
                                .map(newTrackingCode -> {
                                    complaint.setTrackingCode(newTrackingCode);
                                    return this.complaintPersistence.update(complaint);
                                })
                );
    }
    private Boolean isModifiedString(String modifiedValue,String currentValue){
        return (modifiedValue != null && modifiedValue.isEmpty() && !modifiedValue.equals(currentValue));
    }

    private Boolean isModifiedComplaintState(ComplaintState modifiedValue){
        return (modifiedValue != null && !modifiedValue.equals(ComplaintState.CLOSED));
    }

    private Mono<Void> assertComplaintWithBarcodeAndUserMobileAndStateNotExists(String userMobile,String barcode, ComplaintState complaintState){
        return this.complaintPersistence.findByUserMobileAndBarcodeAndState(userMobile,barcode,complaintState)
                .flatMap(complaint -> Mono.error(new ConflictException("There is already a complaint for the article, user and state provided")));
    }
}
