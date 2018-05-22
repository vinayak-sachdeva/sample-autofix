package hello.helper;

import lombok.Getter;
import lombok.Setter;

public class GetResponseIssue {
    @Getter @Setter private Integer id;
    @Getter @Setter private String issueType;
    @Getter @Setter private String line;
    @Getter @Setter private String file;
    @Getter @Setter private String columnName;
    @Getter @Setter private Boolean isFixed;

    public GetResponseIssue(Integer id, String issueType, String line, String file, String columnName, Boolean isFixed) {
        this.id = id;
        this.issueType = issueType;
        this.line = line;
        this.file = file;
        this.columnName = columnName;
        this.isFixed = isFixed;
    }
}