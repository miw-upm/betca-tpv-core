package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ComplaintEntity {
    @Id
    private String id;

    @DBRef(lazy = true)
    private ArticleEntity article;

    private LocalDateTime registrationDate;

    private String description;

    private String reply;

    private String userMobile;
    @Enumerated(EnumType.STRING)
    private ComplaintState state;

    public Complaint toComplaint(){
        Complaint complaint =new Complaint();

        BeanUtils.copyProperties(this,complaint);
        complaint.setBarcode(this.article.getBarcode());
        return complaint;

    }

}
