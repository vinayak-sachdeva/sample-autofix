package hello.helper;

public class GetResponseIssue {
    private Integer id;
    private String issueType;
    private String line;
    private String file;
    private String columnName;
    private Boolean isFixed;

    public GetResponseIssue(Integer id, String issueType, String line, String file, String columnName, Boolean isFixed) {
        this.id = id;
        this.issueType = issueType;
        this.line = line;
        this.file = file;
        this.columnName = columnName;
        this.isFixed = isFixed;
    }
}