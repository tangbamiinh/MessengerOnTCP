package hanu.npr.messengerserver.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PrivateConversation extends Conversation {

    @ManyToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "username")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "username")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PrivateConversation that = (PrivateConversation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
