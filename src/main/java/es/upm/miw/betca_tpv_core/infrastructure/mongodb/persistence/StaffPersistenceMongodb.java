package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.StaffDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.LoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StaffPersistenceMongodb implements StaffPersistence {

    private final StaffDao staffDao;

    @Autowired
    public StaffPersistenceMongodb(StaffDao staffDao) {
        this.staffDao = staffDao;
    }
    @Override
    public Mono<LoginOrder> saveLogin(LoginOrder loginOrder) {
        return staffDao.save(LoginEntity.fromLoginOrder(loginOrder))
                .map(LoginEntity::toLoginOrder);
    }

    @Override
    public Mono<Login> saveLogout(Login login) {
        return staffDao.save(LoginEntity.fromLogin(login))
                .map(LoginEntity::toLogin);
    }

    @Override
    public Mono<Login> findLastLogin(String phone) {
        return staffDao.findTopByPhoneOrderByLoginDateDesc(phone)
                .map(LoginEntity::toLogin);
    }
}
