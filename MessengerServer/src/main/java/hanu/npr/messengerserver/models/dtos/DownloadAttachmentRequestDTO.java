package hanu.npr.messengerserver.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DownloadAttachmentRequestDTO extends BaseDTO {

    public static final String TYPE = "DownloadAttachmentRequest";
    private Long attachmentId;

    @Override
    public String getType() {
        return TYPE;
    }
}
