/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package newdbclass;

public class SelectedComment {

    private int fieldId;            // one of the primary key, user cannot change it here
    private int exCommentId;        // user can select expanded comment here


    /**
     * description: if expanded comment has not been set, exCommentId will be zero
     * and exCommentName will be null
     */
    public SelectedComment(int fieldId, int exCommentId) {
        this.fieldId = fieldId;
        this.exCommentId = exCommentId;
    }

    public SelectedComment() {

    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getExCommentId() {
        return exCommentId;
    }

    public void setExCommentId(int exCommentId) {
        this.exCommentId = exCommentId;
    }

}
