package hanu.npr.messengerclient.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Conversation {

    private Long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<ChatMessage> chatMessages = new ArrayList<>();

    public void addMessage(ChatMessage chatMessage) {
        this.latestMessage = chatMessage;
        chatMessage.setConversation(this);
        this.chatMessages.add(chatMessage);
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ChatMessage latestMessage;

    public String getTitle(String currentLoggedInUsername) {
        if (this instanceof GroupConversation) {
            return ((GroupConversation) this).getName();
        } else if (this instanceof PrivateConversation) {
            User otherUser = ((PrivateConversation) this).getUser1();

            if (otherUser.getUsername().equals(currentLoggedInUsername))
                otherUser = ((PrivateConversation) this).getUser2();
            return otherUser.getFullName();
        }
        return null;
    }
}
