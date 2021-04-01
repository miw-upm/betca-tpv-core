package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.services.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(StaffResource.STAFF)
public class StaffResource {
    public static final String STAFF = "/staff";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    private final StaffService staffService;

    @Autowired
    public StaffResource(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping(LOGIN)
    public Mono<LoginOrder> login(Authentication authentication) {
        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"))) {
            return this.staffService.login((String) authentication.getPrincipal());
        }
        return Mono.empty();
    }

    @PostMapping(LOGOUT)
    public Mono<Login> logout(Authentication authentication) {
        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"))) {
            return this.staffService.logout((String) authentication.getPrincipal());
        }
        return Mono.empty();
    }
}
