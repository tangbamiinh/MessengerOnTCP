package hanu.npr.messengerclient.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]+$", Pattern.CASE_INSENSITIVE);

    private String username;

    private String password;

    private String fullName;

    @Override
    public String toString() {
        return fullName;
    }
}
