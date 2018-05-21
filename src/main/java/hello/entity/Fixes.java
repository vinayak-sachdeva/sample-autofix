package hello.entity;

import javax.persistence.*;

@Entity
public class Fixes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer fixId;

    @ManyToOne
    @JoinColumn(name = "issueId", referencedColumnName = "issueId")
    private Issue issueId;

    private String sandBoxUrl;
    private String line;
    private String file;
    private String columnName;
    private Boolean fixed;
    private String s3Link;

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

    public Integer getFixId() {
        return fixId;
    }

    public Integer getissueId() {
        return issueId.getIssueId();
    }

    public String getSandBoxUrl() {
        return sandBoxUrl;
    }

    public String getLine() {
        return line;
    }

    public String getFile() {
        return file;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getS3Link() {
        return s3Link;
    }

    public Boolean isFixed() {
        return fixed;
    }
}
