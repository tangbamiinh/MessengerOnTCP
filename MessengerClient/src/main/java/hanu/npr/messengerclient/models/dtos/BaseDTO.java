package hanu.npr.messengerclient.models.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;

public abstract class BaseDTO {
    @JsonGetter("type")
    public abstract String getType();
}
