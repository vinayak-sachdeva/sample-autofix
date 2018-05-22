package hello.exceptions;

import hello.helper.CodeGenPostRequest;

public class CodeGenPostRequestException extends Exception {
    private String message;

    public CodeGenPostRequestException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
