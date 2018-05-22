package hello.helper;

import lombok.Getter;
import lombok.Setter;

public class CodeGenGetResponse {
    @Getter @Setter public String status;
    @Getter @Setter public String Url;

    public CodeGenGetResponse(String status, String Url) {
        this.status = status;
        this.Url = Url;
    }
}