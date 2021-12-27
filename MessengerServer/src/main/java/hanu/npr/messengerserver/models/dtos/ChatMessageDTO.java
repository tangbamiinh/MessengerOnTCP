package hanu.npr.messengerserver.models.dtos;

import hanu.npr.messengerserver.models.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO extends BaseDTO {

    public static final String TYPE = "ChatMessage";

    private Long id;

    private Long conversationId;

    private String senderUsername;

    private String content;

    private Attachment attachment;

    @Override
    public String getType() {
        return TYPE;
    }
}
