package hello.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Fixes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private Integer id;

    @Getter @Setter private Integer fixId;

    @ManyToOne
    @JoinColumn(name = "issueId", referencedColumnName = "issueId")
    @Getter @Setter private Issue issueId;

    @Getter @Setter private String sandBoxUrl;
    @Getter @Setter private String line;
    @Getter @Setter private String file;
    @Getter @Setter private String columnName;
    @Getter @Setter private Boolean fixed;
    @Getter @Setter private String s3Link;

    public Fixes() {}

    public Fixes(Integer fixId, Issue issueId, String sandBoxUrl, String line, String file, String columnName, Boolean fixed) {
        this.fixId = fixId;
        this.issueId = issueId;
        this.sandBoxUrl = sandBoxUrl;
        this.line = line;
        this.file = file;
        this.columnName = columnName;
        this.fixed = fixed;
    }

    public Fixes(Integer fixId, Issue issueId, String sandBoxUrl, Boolean fixed) {
        this.fixId = fixId;
        this.issueId = issueId;
        this.sandBoxUrl = sandBoxUrl;
        this.fixed = fixed;
    }

    public Integer getissueId() {
        return issueId.getIssueId();
    }

    public Boolean isFixed() {
        return fixed;
    }
}
