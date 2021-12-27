package hanu.npr.messengerserver.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUserDTO extends BaseDTO {

    public static final String TYPE = "LoggedInUser";

    private String fullName;

    private String username;

    @Override
    public String getType() {
        return TYPE;
    }
}
