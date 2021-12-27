package hanu.npr.messengerclient.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.Base64;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Attachment {

    private Long id;

    private String fileName;

    private String fileType;

    @JsonSerialize(using = FileToBase64Serializer.class, as = String.class)
    @JsonDeserialize(using = Base64ToFileDeserializer.class)
    private byte[] file;
}

class FileToBase64Serializer extends JsonSerializer<byte[]> {
    @Override
    public void serialize(byte[] fileContent,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeObject(Base64.getEncoder().encodeToString(fileContent));
    }
}

class Base64ToFileDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }
}

