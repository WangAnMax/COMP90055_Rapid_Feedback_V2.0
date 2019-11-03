/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.feedback.Activity_Login;
import com.example.feedback.Activity_Record_Voice;
import com.example.feedback.R;

import java.math.BigDecimal;
import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Assessment;
import newdbclass.ExpandedComment;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Remark;
import newdbclass.SelectedComment;

public class Activity_Send_Report_Group extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfGroup;
    private int indexOfStudent;
    private ArrayList<ProjectStudent> studentInfoArrayList;
    private Toolbar mToolbar;
    private String from;
    public static final String FROMREALTIMESEND = "realtime_send";
    public static final String FROMREVIEWSEND = "review_send";
    private Handler handler;
    private Project project;
    private ProjectStudent student;
    private Remark remark;
    private ArrayList<Remark> remarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_group);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        from = intent.getStringExtra("from");
        studentInfoArrayList = new ArrayList<ProjectStudent>();
        for (int i = 0; i < project.getStudentList().size(); i++) {
            if (project.getStudentList().get(i).getGroupNumber() == indexOfGroup)
                studentInfoArrayList.add(project.getStudentList().get(i));
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_send_report_group);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Send_Report_Group.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Send_Report_Group.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void onBackPressed() {
        finish();
    }

    private void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 130:
                        Toast.makeText(Activity_Send_Report_Group.this, "Successfully send end final result", Toast.LENGTH_SHORT).show();
                        break;
                    case 131:
                        Toast.makeText(Activity_Send_Report_Group.this, "Fail to send the final result", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);

        student = AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentList().get(indexOfStudent);
        remarkList = student.getRemarkList();

        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == AllFunctions.getObject().getId()) {
                remark = remarkList.get(i);
                break;
            }
        }

        Button button_record = findViewById(R.id.button_record_group);
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Record_Voice.class);
                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                intent.putExtra("from", from);
                startActivity(intent);
            }
        });
        Button button_sendSingle = findViewById(R.id.button_sendStudent_sendReportGroup);
        button_sendSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendEmail(project.getId(), studentInfoArrayList.get(i).getId(), 1);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendEmail(project.getId(), studentInfoArrayList.get(i).getId(), 1);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button button_sendBoth = findViewById(R.id.button_sendBoth_sendReportGroup);
        button_sendBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendEmail(project.getId(), studentInfoArrayList.get(i).getId(), 2);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendEmail(project.getId(), studentInfoArrayList.get(i).getId(), 2);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button button_finish = findViewById(R.id.btn_finish_sendReportGroup);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });

        TextView textView_totalMark = findViewById(R.id.textView_totalMark_sendReportGroup);
        textView_totalMark.setText("Mark:" + (int) getAverageMark(remarkList) + "%");

        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getName() + "</h1>" +
                        "<hr>" +
                        "<p>" + "Group: " + indexOfGroup + "</p >" + "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + getAverageMark(remarkList) + "%</p >";

        htmlString += "<h2 style=\"font-weight: normal\">Students</h2>" + "<p>";
        for (int i = 0; i < studentInfoArrayList.size(); i++)
            htmlString = htmlString + studentInfoArrayList.get(i).getStudentNumber() + "---" + studentInfoArrayList.get(i).getFirstName() + " " + studentInfoArrayList.get(i).getMiddleName() + " " + studentInfoArrayList.get(i).getLastName() + "<br>";

        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\">Grading Criteria</h2>" + "<p>";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + getCriterionName(remark.getAssessmentList().get(i)) + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + getAverageCriterionMark(remarkList, remark.getAssessmentList().get(i).getCriterionId()) + "/" + getCriterionMaxMark(remark.getAssessmentList().get(i)) + "</span></h3>";
            for (int j = 0; j < remarkList.size(); j++) {
                htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + "Marker " + (j + 1) + ":</h4>";
                if (remarkList.get(j).getAssessmentList().size() > 0) {
                    for (int k = 0; k < remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().size(); k++) {
                        htmlString += "<p>" + getFieldName(remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().get(k)) +
                                " : " + getExCommentName(remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().get(k)) + "</p >";
                    }
                }
            }
            htmlString += "<br>";
        }

        htmlString += "<h2 style=\"font-weight: normal\"><span style=\"float:left\">" + "Remark" + "</span></h2>";
        for (int i = 0; i < studentInfoArrayList.size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + "For " + getStudentName(studentInfoArrayList.get(i)) + "</span></h3>";
            for (int j = 0; j < studentInfoArrayList.get(i).getRemarkList().size(); j++) {
                htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + "Marker " + (j + 1) + ":</h4>";
                if (studentInfoArrayList.get(i).getRemarkList().get(j).getText() == null) {
                    htmlString += "<p>" + "No remark." + "</p >";
                } else {
                    htmlString += "<p>" + studentInfoArrayList.get(i).getRemarkList().get(j).getText() + "</p >";
                }
            }
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_sendReportGroup);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }

    public String getStudentName(ProjectStudent student) {
        return student.getLastName() + " " + student.getMiddleName() + " " + student.getFirstName();
    }

    public String getFinalRemark(ArrayList<Remark> remarkList) {
        Remark remark = new Remark();
        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == project.getPrincipalId()) {
                remark = remarkList.get(i);
            }
        }

        String finalRemark = "";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            finalRemark += "Critrion" + (i + 1) + ": " + getCriterionName(remark.getAssessmentList().get(i));
            finalRemark += "\n";
            for (int k = 0; k < remark.getAssessmentList().get(i).getSelectedCommentList().size(); k++) {
                finalRemark += getFieldName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) +
                        " : " + getExCommentName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) + "\n";
            }
        }
        return finalRemark;
    }

    public double getAverageCriterionMark(ArrayList<Remark> remarkList, int criterionId) {
        double sumMark = 0;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            Remark remark = remarkList.get(i);
            for (int j = 0; j < remark.getAssessmentList().size(); j++) {
                if (remark.getAssessmentList().get(j).getCriterionId() == criterionId) {
                    sumMark += remark.getAssessmentList().get(j).getScore();
                }
            }
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }

    public double getAverageMark(ArrayList<Remark> remarkList) {
        double sumMark = 0;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            sumMark += getTotalMark(remarkList.get(i));
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }

    public double getTotalMark(Remark remark) {
        Double sum = 0.0;

        double totalWeight = 0;
        int totalWeighting = 0;

        for (int j = 0; j < project.getCriterionList().size(); j++) {
            totalWeight = totalWeight + project.getCriterionList().get(j).getMaximumMark();
        }
        totalWeighting = Double.valueOf(totalWeight).intValue();

        for (int m = 0; m < remark.getAssessmentList().size(); m++) {
            sum = sum + remark.getAssessmentList().get(m).getScore();
        }
        sum = sum * (100.0 / totalWeighting);

        return sum;
    }

    public String getCriterionName(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getName();
            }
        }
        return "";
    }

    public double getCriterionMaxMark(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getMaximumMark();
            }
        }
        return 0;
    }

    public String getFieldName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                if (project.getCriterionList().get(i).getFieldList().get(j).getId() == selectedComment.getFieldId()) {
                    return project.getCriterionList().get(i).getFieldList().get(j).getName();
                }
            }
        }
        return "";
    }

    public String getExCommentName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                for (int k = 0; k < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().size(); k++) {
                    for (int m = 0; m < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().size(); m++) {
                        ExpandedComment expandedComment = project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().get(m);
                        if (expandedComment.getId() == selectedComment.getExCommentId()) {
                            return expandedComment.getText();
                        }
                    }
                }
            }
        }
        return "";
    }
}

