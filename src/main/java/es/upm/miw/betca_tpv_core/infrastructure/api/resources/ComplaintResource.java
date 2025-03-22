package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.services.ComplaintService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintCreationDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(ComplaintResource.COMPLAINTS)
public class ComplaintResource {
    public static final String COMPLAINTS = "/complaints";
    public static final String SEARCH = "/search";
    public static final String COMPLAINT_TRACKING_CODE = "/{trackingCode}";
    private final ComplaintService complaintService;

    @Autowired
    public ComplaintResource(ComplaintService complaintService){
        this.complaintService=complaintService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping(produces = {"application/json"})
    public Mono<Complaint> create(@Valid @RequestBody ComplaintCreationDto complaintCreationDto,Authentication authentication){
        return this.complaintService.create(complaintCreationDto.toComplaint(),authentication);
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CUSTOMER','OPERATOR')")
    @GetMapping(COMPLAINT_TRACKING_CODE)
    public Mono<Complaint> readByTrackingCode(@PathVariable String trackingCode ,Authentication authentication){
        return this.complaintService.readByTrackingCode(trackingCode,authentication);
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR') or #userMobile == authentication.principal")
    @GetMapping(SEARCH)
    public Flux<Complaint> findByUserMobileNullSafe(@RequestParam(required = false) String userMobile){
        return this.complaintService.findByUserMobileNullSafe(userMobile);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @DeleteMapping(COMPLAINT_TRACKING_CODE)
    public Mono<Void> delete(@PathVariable String trackingCode, Authentication authentication){
        return this.complaintService.delete(trackingCode,authentication);
    }
}
