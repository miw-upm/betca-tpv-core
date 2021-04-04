package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.slack.SlackMessagePublisher;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SlackMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SlackMessageService {
    private SlackMessagePublisher slackMessagePublisher;

    @Autowired
    SlackMessageService(SlackMessagePublisher slackMessagePublisher) {
        this.slackMessagePublisher = slackMessagePublisher;
    }

    public Mono<SlackMessage> create(SlackMessageDto slackMessageDto) {
        SlackMessage slackMessage = this.createMessageFromDto(slackMessageDto);
        return this.slackMessagePublisher.create(slackMessage);
    }

    public  Mono<Cashier> createCloseCahierMessage(Cashier cashier, SlackMessageDto slackMessageDto) {
        SlackMessage slackMessage = this.createMessageFromDto(slackMessageDto);
        return this.slackMessagePublisher.createCloseCashierMessage(cashier, slackMessage);
    }

    public SlackMessage createMessageFromDto(SlackMessageDto slackMessageDto) {
        SlackMessageText headerText = new SlackMessageText("plain_text", slackMessageDto.getTitle());
        SlackMessageBlock headerBlock = new SlackMessageBlock("header", headerText);
        SlackMessageText contentText = new SlackMessageText("mrkdwn", slackMessageDto.getText());
        SlackMessageAccessory contentAccessory = new SlackMessageAccessory("image", "https://avatars.githubusercontent.com/u/5365410?v=4/wikipedia/commons/9/9d/UPM_Logo_Blog.png", "Logo UPM");
        SlackMessageContentBlock contentBlock = new SlackMessageContentBlock("section", contentText, contentAccessory);
        SlackMessageAttachment slackMessageAttachment = new SlackMessageAttachment(slackMessageDto.getStatus(), List.of(headerBlock, contentBlock));

        return new SlackMessage(List.of(slackMessageAttachment));
    }
}
