package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.model.StaffTime;
import es.upm.miw.betca_tpv_core.domain.model.StaffTimeOrder;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    public Stream<StaffTime> findByMonth(StaffTimeOrder staffTimeOrder) {
        Map<Month, Double> hoursPerMonth = new HashMap<>();
        List<StaffTime> staffTimeList = new ArrayList<>();
        staffPersistence.findInRange(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate())
                .forEach(login -> {
                    Double actual;
                    actual = hoursPerMonth.getOrDefault(login.getLoginDate().getMonth(), 0.0);
                    hoursPerMonth.put(login.getLoginDate().getMonth(), actual + login.getTotal());
                });

        hoursPerMonth.forEach((month, aDouble) -> staffTimeList.add(StaffTime.builder()
                .hours(aDouble)
                .time(month.name())
                .build()));
        return staffTimeList.stream();
    }

    public Stream<StaffTime> findByDays(StaffTimeOrder staffTimeOrder) {
        Map<Integer, Double> hoursPerMonth = new HashMap<>();
        List<StaffTime> staffTimeList = new ArrayList<>();
        staffPersistence.findInRange(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate())
                .forEach(login -> {
                    Double actual;
                    actual = hoursPerMonth.getOrDefault(login.getLoginDate().getDayOfYear(), 0.0);
                    hoursPerMonth.put(login.getLoginDate().getDayOfYear(), actual + login.getTotal());
                });

        hoursPerMonth.forEach((dayOfYear, aDouble) -> staffTimeList.add(StaffTime.builder()
                .hours(aDouble)
                .time(Year.of(Year.now().getValue()).atDay(dayOfYear).toString())
                .build()));
        return staffTimeList.stream();
    }
}
