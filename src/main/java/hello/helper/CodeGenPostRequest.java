package hello.helper;

import java.util.HashSet;
import java.util.Set;

public class CodeGenPostRequest {
    String url;
    String username;
    String password;
    public Set<Integer> fileIds;

    public CodeGenPostRequest(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.fileIds = new HashSet<Integer>();
    }
}