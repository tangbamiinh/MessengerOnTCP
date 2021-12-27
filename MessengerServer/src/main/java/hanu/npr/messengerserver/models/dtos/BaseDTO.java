package hanu.npr.messengerserver.models.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseDTO {
    @JsonGetter("type")
    public abstract String getType();
}
