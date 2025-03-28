package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class ReviewEntity {

    @Id
    private String id;
    private String userId;
    private String articleId;
    private Integer stars;
    private String opinion;
}