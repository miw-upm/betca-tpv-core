package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;


public class SlackMessageDto {

    private String level;
    private String text;

    public SlackMessageDto() {

    }
    public SlackMessageDto(String level, String text) {
        this.level = level;
        this.text = text;
    }

    // Getter/Setter

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
