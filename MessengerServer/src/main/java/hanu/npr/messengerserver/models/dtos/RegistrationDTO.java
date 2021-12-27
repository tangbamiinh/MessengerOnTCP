package hanu.npr.messengerserver.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationDTO extends BaseDTO {

    public static final String TYPE = "Registration";

    private String fullName;

    private String username;

    private String password;

    @Override
    public String getType() {
        return TYPE;
    }
}
