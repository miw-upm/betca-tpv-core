package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class StaffService {

    private final StaffPersistence staffPersistence;

    @Autowired
    public StaffService(StaffPersistence staffPersistence) {
        this.staffPersistence = staffPersistence;
    }

    public Mono<LoginOrder> login(String phone) {
        LoginOrder loginOrder = LoginOrder.builder()
                .loginDate(LocalDateTime.now())
                .phone(phone)
                .build();
        return staffPersistence.saveLogin(loginOrder);
    }

    public Mono<Login> logout(String phone) {
        return staffPersistence.findLastLogin(phone)
                .switchIfEmpty(Mono.error(new NotFoundException("Not found login for user: " + phone)))
                .map(login -> {
                    login.setLogoutDate(LocalDateTime.now());
                    return login;
                })
                .flatMap(staffPersistence::saveLogout);
    }
}
