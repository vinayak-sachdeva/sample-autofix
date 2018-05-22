package hello.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    @Getter @Setter private Integer issueId;
    @Getter @Setter private String issueType;
}
