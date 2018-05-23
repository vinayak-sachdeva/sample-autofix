package hello.helper;

import lombok.Getter;
import lombok.Setter;

public class CodeGenPostResponse {
    @Getter @Setter private Integer id;

    public CodeGenPostResponse(Integer id) {
        this.id = id;
    }
}