package hanu.npr.messengerclient.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private Long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Conversation conversation;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User sender;

    private String content;

    private Attachment attachment;

    @JsonIgnoreProperties
    private boolean isMyMessage;
}