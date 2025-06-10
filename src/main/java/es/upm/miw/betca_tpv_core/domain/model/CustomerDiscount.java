package es.upm.miw.betca_tpv_core.domain.model;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;


public class CustomerDiscount {

    private User user;
    private String note;
    private LocalDateTime registrationDate;
    private Integer discount;
    private Integer minimumPurchase;

    public CustomerDiscount() {

    }

    public CustomerDiscount(User user, String note, LocalDateTime registrationDate
            , Integer discount, Integer minimumPurchase) {
        this.user = user;
        this.note = note;
        this.registrationDate = registrationDate;
        this.discount = discount;
        this.minimumPurchase = minimumPurchase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getMinimumPurchase() {
        return minimumPurchase;
    }

    public void setMinimumPurchase(Integer minimumPurchase) {
        this.minimumPurchase = minimumPurchase;
    }

    public CustomerDiscountEntity toEntity() {
        CustomerDiscountEntity customerDiscountEntity = new CustomerDiscountEntity();
        BeanUtils.copyProperties(this, customerDiscountEntity, "user");
        User user =  new User();
        BeanUtils.copyProperties(this.user, user);
        customerDiscountEntity.setUser(user);
        return customerDiscountEntity;
    }

    @Override
    public String toString() {
        return "CustomerDiscount{" +
                "user='" + user + '\'' +
                ", note='" + note + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", discount=" + discount +
                ", minimumPurchase=" + minimumPurchase +
                '}';
    }

}