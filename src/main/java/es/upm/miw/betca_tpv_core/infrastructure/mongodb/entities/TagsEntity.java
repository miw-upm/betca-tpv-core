package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "tags")
public class TagsEntity {
   @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String group;
    
    private String description;
    
    public TagEntity(Tag tag) {
        BeanUtils.copyProperties(tag, this);
    }
    
    public Tag toTag() {
        Tag tag = new Tag();
        BeanUtils.copyProperties(this, tag);
        return tag;
    }
}
