package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
public class CustomerDiscountEntity {
    @Id
    private String id;
    private String note;
    private LocalDateTime registrationDate;
    private Double discount;
    private Double minimumPurchase;
    private String userPhone;

    public CustomerDiscountEntity(CustomerDiscount customerDiscount) {
        BeanUtils.copyProperties(customerDiscount, this);
    }

    public CustomerDiscount toCustomerDiscount() {
        CustomerDiscount customerDiscount = new CustomerDiscount();
        BeanUtils.copyProperties(this, customerDiscount);
        return customerDiscount;
    }
}
