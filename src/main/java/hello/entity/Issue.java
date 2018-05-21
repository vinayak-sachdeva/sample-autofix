package hello.entity;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    private Integer issueId;
    private String issueType;

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getIssueType() {
        return issueType;
    }

}
