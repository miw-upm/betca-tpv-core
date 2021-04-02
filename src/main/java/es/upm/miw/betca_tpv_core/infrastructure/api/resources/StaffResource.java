package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.services.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(StaffResource.STAFF)
public class StaffResource {
    public static final String STAFF = "/staff";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String TIME = "/time";
    public static final String REPORTS = "/reports";

    private final StaffService staffService;

    @Autowired
    public StaffResource(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping(LOGIN)
    public Mono<LoginOrder> login(Authentication authentication) {
        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR") || a.getAuthority().equals("ROLE_MANAGER"))) {
            return this.staffService.login((String) authentication.getPrincipal());
        }
        return Mono.empty();
    }

    @PostMapping(LOGOUT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
    public Mono<Login> logout(Authentication authentication) {
        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR") || a.getAuthority().equals("ROLE_MANAGER"))) {
            return this.staffService.logout((String) authentication.getPrincipal());
        }
        return Mono.empty();
    }

    @GetMapping(TIME)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
    public Flux<StaffTime> findTime(@RequestParam String mobile,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate startDate,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate endDate,
                                      @RequestParam String typeOfSearch) {
        StaffTimeOrder staffTimeOrder = StaffTimeOrder.builder()
                .phone(mobile)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        if(typeOfSearch.equals("day")) return staffService.findByDays(staffTimeOrder);
        return staffService.findByMonth(staffTimeOrder);
    }

    @GetMapping(REPORTS)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
    public Flux<StaffReport> findReports(@RequestParam String month) {
        return staffService.findReports(month);
    }
}
