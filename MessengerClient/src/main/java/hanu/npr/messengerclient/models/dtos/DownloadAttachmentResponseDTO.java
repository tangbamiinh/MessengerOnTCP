package hanu.npr.messengerclient.models.dtos;

import hanu.npr.messengerclient.models.Attachment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DownloadAttachmentResponseDTO extends BaseDTO {

    public static final String TYPE = "DownloadAttachmentResponse";

    private Attachment attachment;

    @Override
    public String getType() {
        return TYPE;
    }
}
