package hanu.npr.messengerclient.models;

import lombok.Data;

@Data
public class Attachment {

    private Long id;

    private String fileName;

    private String fileType;

    private byte[] file;
}
