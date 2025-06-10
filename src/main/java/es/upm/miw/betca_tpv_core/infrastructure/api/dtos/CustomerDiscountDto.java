package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;



import java.time.LocalDateTime;

public class CustomerDiscountDto {
    private String userMobile;
    private String note;
    private LocalDateTime registrationDate;
    private Integer discount;
    private Integer minimumPurchase;

    public  CustomerDiscountDto() {

    }

    public CustomerDiscountDto(String userMobile, String note, LocalDateTime registrationDate,
                               Integer discount, Integer minimumPurchase){
        this.userMobile = userMobile;
        this.note = note;
        this.registrationDate = registrationDate;
        this.discount = discount;
        this.minimumPurchase = minimumPurchase;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}
