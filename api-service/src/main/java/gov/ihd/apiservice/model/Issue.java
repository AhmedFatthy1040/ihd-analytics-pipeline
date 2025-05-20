package gov.ihd.apiservice.model;

import lombok.Data;

@Data
public class Issue {
    private int issue_id;
    private IssueClass issue_class;
    
    // Getter for snake_case field name
    public int getIssue_id() {
        return issue_id;
    }
    
    public IssueClass getIssue_class() {
        return issue_class;
    }
}
