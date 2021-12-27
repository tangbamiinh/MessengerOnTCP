package hanu.npr.messengerserver.models.dtos;

import hanu.npr.messengerserver.models.GroupConversation;
import hanu.npr.messengerserver.models.PrivateConversation;
import hanu.npr.messengerserver.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitialDataDTO extends BaseDTO {

    public static final String TYPE = "InitialData";

    private List<User> onlineUsers;

    private List<GroupConversation> groupConversations;

    private List<PrivateConversation> privateConversations;

    @Override
    public String getType() {
        return TYPE;
    }
}
