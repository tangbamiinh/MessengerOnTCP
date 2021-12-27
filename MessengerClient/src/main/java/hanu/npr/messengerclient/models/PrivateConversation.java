package hanu.npr.messengerclient.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateConversation extends Conversation {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user1;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user2;
}
