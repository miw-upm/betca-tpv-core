package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Collections;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class StockAuditUpdateDto {
    private List<ArticleAuditDto> articlesAudited;

    public List<ArticleAuditDto> getArticlesAudited() {
        return articlesAudited != null ? articlesAudited : Collections.emptyList();
    }

    public void setArticlesAudited(List<ArticleAuditDto> articlesAudited) {
        this.articlesAudited = articlesAudited;
    }
}