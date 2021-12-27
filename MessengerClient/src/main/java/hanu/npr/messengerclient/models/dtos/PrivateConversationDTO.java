package hanu.npr.messengerclient.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateConversationDTO extends BaseDTO {

    public static final String TYPE = "PrivateConversation";

    private Long id;

    private String username1;
    private String fullName1;

    private String username2;
    private String fullName2;

    @Override
    public String getType() {
        return TYPE;
    }
}
