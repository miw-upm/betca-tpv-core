package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StaffReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public class StaffPersistenceMongodb implements StaffPersistence {

    private final StaffReactive staffReactive;

    @Autowired
    public StaffPersistenceMongodb(StaffReactive staffReactive) {
        this.staffReactive = staffReactive;
    }
    @Override
    public Mono<LoginOrder> saveLogin(LoginOrder loginOrder) {
        return staffReactive.save(LoginEntity.fromLoginOrder(loginOrder))
                .map(LoginEntity::toLoginOrder);
    }

    @Override
    public Mono<Login> saveLogout(Login login) {
        return staffReactive.save(LoginEntity.fromLogin(login))
                .map(LoginEntity::toLogin);
    }

    @Override
    public Mono<Login> findLastLogin(String phone) {
        return staffReactive.findTopByPhoneOrderByLoginDateDesc(phone)
                .map(LoginEntity::toLogin);
    }

    @Override
    public Flux<LoginOrder> findByLoginDateAndPhone(LocalDate loginDate, String phone) {
        return staffReactive.findByLoginDateBetweenAndPhone(loginDate.atStartOfDay(), loginDate.atTime(LocalTime.MAX), phone)
                .map(LoginEntity::toLoginOrder);
    }

    @Override
    public Flux<Login> findInRangeByPhone(LocalDate startDate, LocalDate endDate, String phone) {
        return staffReactive.findByLoginDateBetweenAndPhone(startDate.atStartOfDay(), endDate.atStartOfDay(), phone)
                .map(LoginEntity::toLogin);
    }

    @Override
    public Flux<Login> findInRange(LocalDate startDate, LocalDate endDate) {
        return staffReactive.findByLoginDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay())
                .map(LoginEntity::toLogin);
    }
}
