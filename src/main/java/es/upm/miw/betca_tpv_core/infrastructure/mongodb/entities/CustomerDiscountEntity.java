package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class CustomerDiscountEntity {
    @Id
    private String id;
    private User user;
    private String note;
    private LocalDateTime registrationDate;
    private Integer discount;
    private Integer minimumPurchase;


    public CustomerDiscountEntity() {

    }

    public CustomerDiscountEntity(User user, String note, LocalDateTime registrationDate
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


    public CustomerDiscount toCustomerDiscount() {
        CustomerDiscount customerDiscount = new CustomerDiscount();
        BeanUtils.copyProperties(this, customerDiscount, "user");
        User user =  new User();
        BeanUtils.copyProperties(this.user, user);
        customerDiscount.setUser(user);
        return customerDiscount;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && (id.equals(((CustomerDiscountEntity) obj).id));
    }

}
