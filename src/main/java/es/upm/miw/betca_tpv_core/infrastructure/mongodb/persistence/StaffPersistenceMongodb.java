package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StaffReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.StaffDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Stream;

@Repository
public class StaffPersistenceMongodb implements StaffPersistence {

    private final StaffReactive staffReactive;
    private final StaffDao staffDao;

    @Autowired
    public StaffPersistenceMongodb(StaffReactive staffReactive, StaffDao staffDao) {
        this.staffReactive = staffReactive;
        this.staffDao = staffDao;
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
    public Stream<Login> findInRange(LocalDate startDate, LocalDate endDate) {
        return staffDao.findByLoginDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay())
                .map(LoginEntity::toLogin);
    }
}
