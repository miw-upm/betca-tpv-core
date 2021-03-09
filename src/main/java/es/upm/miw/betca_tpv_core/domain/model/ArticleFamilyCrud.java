package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleFamilyCrud {
    private String reference;
    private String description;
    private TreeType treeType;
    @Singular("articleFamilyCrud")
    private List<ArticleFamilyCrud> articleFamilyCrudList = new ArrayList<>();
}
