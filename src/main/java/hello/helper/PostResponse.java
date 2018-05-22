package hello.helper;

public class PostResponse {
    private Integer fixId;
    private String status;
    private String message;

    public PostResponse(String message) {
        this.message = message;
        this.status = "500";
    }

    public PostResponse(Integer fixId, String status) {
        this.fixId = fixId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
}