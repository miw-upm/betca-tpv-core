package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleFamilyCrud {
    private String id;
    private String reference;
    private String barcode;
    private String parentReference;
    private String description;
    private TreeType treeType;
    @Singular("articleFamilyCrud")
    private List<ArticleFamilyCrud> articleFamilyCrudList = new ArrayList<>();

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUID.randomUUID().toString();
        }
    }
}
