package hello.exceptions;

import lombok.Getter;
import lombok.Setter;

public class CodeGenPostRequestException extends Exception {
    @Getter @Setter private String message;

    public CodeGenPostRequestException(String message) {
        this.message = message;
    }
}
