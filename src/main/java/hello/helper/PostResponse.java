package hello.helper;

import lombok.Getter;
import lombok.Setter;

public class PostResponse {
    @Getter @Setter private Integer fixId;
    @Getter @Setter private String status;
    @Getter @Setter private String message;

    public PostResponse(String message) {
        this.message = message;
        this.status = "500";
    }

    public PostResponse(Integer fixId, String status) {
        this.fixId = fixId;
        this.status = status;
    }
}