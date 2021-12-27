package hanu.npr.messengerserver.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorDTO extends BaseDTO {

    public static final String TYPE = "Error";

    private String message;

    @Override
    public String getType() {
        return TYPE;
    }
}
