package hanu.npr.messengerserver.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserJoinedDTO extends BaseDTO {

    public static final String TYPE = "NewUserJoined";

    private String username;
    private String fullName;

    @Override
    public String getType() {
        return TYPE;
    }
}
