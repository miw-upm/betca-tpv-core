package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.services.ComplaintService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Rest
@RequestMapping(ComplaintResource.COMPLAINTS)
public class ComplaintResource {
    public static final String COMPLAINTS = "/complaints";
    public static final String SEARCH = "/search";
    public static final String USER_MOBILE = "/{userMobile}";
    private final ComplaintService complaintService;
    @Autowired
    public ComplaintResource(ComplaintService complaintService){
        this.complaintService=complaintService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(SEARCH)
    public Flux<Complaint> findByUserMobileNullSafe(@RequestParam(required = false) String userMobile){
        return this.complaintService.findByUserMobileNullSafe(userMobile);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #userMobile == authentication.principal")
    @GetMapping(USER_MOBILE)
    public Flux<Complaint> findByUserMobile(@PathVariable String userMobile){
        return this.complaintService.findByUserMobile(userMobile);
    }


}
