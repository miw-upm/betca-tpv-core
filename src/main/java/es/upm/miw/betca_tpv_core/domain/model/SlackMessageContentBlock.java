package es.upm.miw.betca_tpv_core.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SlackMessageContentBlock extends SlackMessageBlock{

    private SlackMessageAccessory accessory;

    public SlackMessageContentBlock(String section, SlackMessageText contentText, SlackMessageAccessory accessory) {
        super(section, contentText);
        this.accessory = accessory;
    }
}

