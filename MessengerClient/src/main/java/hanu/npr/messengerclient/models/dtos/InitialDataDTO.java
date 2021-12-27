package hanu.npr.messengerclient.models.dtos;

import hanu.npr.messengerclient.models.GroupConversation;
import hanu.npr.messengerclient.models.PrivateConversation;
import hanu.npr.messengerclient.models.User;
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
