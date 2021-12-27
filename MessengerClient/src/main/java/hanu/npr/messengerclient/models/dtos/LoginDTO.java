package hanu.npr.messengerclient.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO extends BaseDTO {

    public static final String TYPE = "Login";

    private String username;

    private String password;

    @Override
    public String getType() {
        return TYPE;
    }
}
