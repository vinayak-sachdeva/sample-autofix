package hello.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class CodeGenPostRequest {
    @Getter @Setter String url;
    @Getter @Setter String username;
    @Getter @Setter String password;
    @Getter @Setter public Set<Integer> fileIds;

    public CodeGenPostRequest(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.fileIds = new HashSet<Integer>();
    }
}