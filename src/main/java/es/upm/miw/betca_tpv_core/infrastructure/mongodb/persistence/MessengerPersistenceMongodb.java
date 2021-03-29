package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.MessengerPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.MessengerReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MessengerPersistenceMongodb implements MessengerPersistence {

    private MessengerReactive messengerReactive;

    @Autowired
    public MessengerPersistenceMongodb(MessengerReactive messengerReactive) {
        this.messengerReactive = messengerReactive;
    }

    @Override
    public Mono<Message> create(Message newMessage) {
        MessageEntity messageEntity = new MessageEntity(newMessage);

        return this.messengerReactive.save(messageEntity).map(MessageEntity::toMessage);
    }

    @Override
    public Flux<Message> findByUserFromNullSafe(User userFrom) {
        return this.messengerReactive.findByUserFrom(userFrom.getMobile()).map(MessageEntity::toMessage);
    }

    @Override
    public Flux<Message> findByUserToNullSafe(User userTo) {
        Flux<Message> userToMessages = this.messengerReactive.findByUserTo(userTo.getMobile())
                .flatMap(messageEntity -> {
                        messageEntity.setIsRead(Boolean.TRUE);
                        return this.messengerReactive.save(messageEntity).map(MessageEntity::toMessage);
                });

        return userToMessages;
    }

    @Override
    public Flux<Message> findNotReadMessages(User userTo) {
        return this.messengerReactive.findByUserToAndIsRead(userTo.getMobile(), Boolean.FALSE).map(MessageEntity::toMessage);
    }
}
