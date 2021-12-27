package hanu.npr.messengerserver.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuccessDTO extends BaseDTO {

    public static final String TYPE = "Success";

    private String message;

    @Override
    public String getType() {
        return TYPE;
    }
}
