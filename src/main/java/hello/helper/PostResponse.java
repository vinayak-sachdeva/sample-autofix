package hello.helper;

import lombok.Getter;
import lombok.Setter;

public class PostResponse {
    @Getter @Setter private Integer fixId;
    @Getter @Setter private String status;
    @Getter @Setter private String message;

    public PostResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public PostResponse(Integer fixId, String status) {
        this.fixId = fixId;
        this.status = status;
    }
}