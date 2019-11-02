/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package util;

public class CustomisedCriterion {
    private String criterion;
    private String field;
    private String comment;
    private String expandedComment;
    private String type;

    @Override
    public String toString() {
        return "CustomisedCriterion{" +
                "criteria='" + criterion + '\'' +
                ", subSection='" + field + '\'' +
                ", shortText='" + comment + '\'' +
                ", longText='" + expandedComment + '\'' +
                ", grade='" + type + '\'' +
                '}';
    }

    public CustomisedCriterion() {
        criterion = "";
        field = "";
        comment = "";
        expandedComment = "";
        type = "";
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setExpandedComment(String expandedComment) {
        this.expandedComment = expandedComment;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCriterion() {
        return criterion;
    }

    public String getField() {
        return field;
    }

    public String getComment() {
        return comment;
    }

    public String getExpandedComment() {
        return expandedComment;
    }

    public String getType() {
        return type;
    }
}
