package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class MessageEntity {
    @Id
    private String id;
    private String subject;
    private String text;
    @DBRef
    private User userFrom;
    @DBRef
    private User userTo;
    private LocalDate creationDate;

    public MessageEntity(Message article) {
        BeanUtils.copyProperties(article, this);
    }

    public Message toMessage() {
        Message message = new Message();
        BeanUtils.copyProperties(this, message);
        return message;
    }
}
