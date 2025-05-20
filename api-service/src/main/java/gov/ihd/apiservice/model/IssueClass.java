package gov.ihd.apiservice.model;

import lombok.Data;

@Data
public class IssueClass {
    private int issue_class_key;
    private String issue_class_code;
    
    // Getters for snake_case field names
    public int getIssue_class_key() {
        return issue_class_key;
    }
    
    public String getIssue_class_code() {
        return issue_class_code;
    }
}
