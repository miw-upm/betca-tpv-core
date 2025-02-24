package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @DBRef
    private ArticleEntity article;

    private LocalDateTime registrationDate;

    private String description;

    private String reply;

    private Number mobile;
    @Enumerated(EnumType.STRING)
    private ComplaintState state;

}
