package hello.helper;

public class PostResponse {
    private Integer fixId;
    private String status;
    public PostResponse(Integer fixId, String status) {
        this.fixId = fixId;
        this.status = status;
    }
}