package hello.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetResponse {
    @Getter @Setter private Integer fixId;
    @Setter private List<GetResponseIssue> issues;
    @Getter @Setter private String s3Link;
    @Getter @Setter private String status;

    public GetResponse(Integer fixId, String s3Link, String status) {
        this.fixId = fixId;
        this.s3Link = s3Link;
        this.status = status;
        this.issues = new ArrayList<GetResponseIssue>();
    }

    public GetResponse(Integer fixId, String status) {
        this.fixId = fixId;
        this.status = status;
        this.s3Link = "";
        this.issues = new ArrayList<GetResponseIssue>();
    }

    public List<GetResponseIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }
}