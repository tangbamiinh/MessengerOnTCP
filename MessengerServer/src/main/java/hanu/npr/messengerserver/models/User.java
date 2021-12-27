package hanu.npr.messengerserver.models;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]+$", Pattern.CASE_INSENSITIVE);

    @Id
    @Column(unique = true, columnDefinition = "VARCHAR(64)")
    private String username;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    private String fullName;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "Participation",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = {@JoinColumn(name = "group_conversation_id") })
    @ToString.Exclude
    @JsonIgnore
    private Collection<GroupConversation> groupConversations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return username != null && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
