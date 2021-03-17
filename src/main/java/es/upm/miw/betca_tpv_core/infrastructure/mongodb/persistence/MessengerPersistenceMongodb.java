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
        return this.messengerReactive.findAll()
                .filter(x -> x.getUserFrom().equals(userFrom.getMobile()))
                .map(MessageEntity::toMessage);
    }

    @Override
    public Flux<Message> findByUserToNullSafe(User userTo) {
        return this.messengerReactive.findAll()
                .filter(x -> x.getUserFrom().equals(userTo.getMobile()))
                .map(MessageEntity::toMessage);
    }

    @Override
    public Flux<Message> findNotReadMessages(User userTo) {
        return this.messengerReactive.findAll()
                .filter(x -> x.getUserFrom().equals(userTo.getMobile()) && !x.isRead())
                .map(MessageEntity::toMessage);
    }
}
