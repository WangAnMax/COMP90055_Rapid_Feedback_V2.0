package com.example.feedback;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.math.BigDecimal;
import java.util.ArrayList;

import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import main.AllFunctions;

public class Activity_Send_Report_Group extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfGroup;
    private int indexOfStudent;
    //    private int indexOfMark;
    private ArrayList<StudentInfo> studentInfoArrayList;
    private Toolbar mToolbar;
    private String from;
    public static final String FROMREALTIMESEND= "realtime_send";
    public static final String FROMREVIEWSEND= "review_send";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_group);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        from = intent.getStringExtra("from");
//        indexOfMark = Integer.parseInt(intent.getStringExtra("indexOfMark"));
        studentInfoArrayList = new ArrayList<StudentInfo>();
        for (int i = 0; i < project.getStudentInfo().size(); i++) {
            if (project.getStudentInfo().get(i).getGroup() == indexOfGroup)
                studentInfoArrayList.add(project.getStudentInfo().get(i));
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_send_report_group);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
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
        Log.d("EEEE", "send report group");

        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        ArrayList<Mark> markList = AllFunctions.getObject().getMarkListForMarkPage();
        Button button_record = findViewById(R.id.button_record_group);
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Record_Voice.class);
                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
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
                        AllFunctions.getObject().sendPDF(project, studentInfoArrayList.get(i).getNumber(), 1);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendPDF(project, studentInfoArrayList.get(i).getNumber(), 1);
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
                        AllFunctions.getObject().sendPDF(project, studentInfoArrayList.get(i).getNumber(), 2);
                    Intent intent = new Intent(Activity_Send_Report_Group.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    for (int i = 0; i < studentInfoArrayList.size(); i++)
                        AllFunctions.getObject().sendPDF(project, studentInfoArrayList.get(i).getNumber(), 2);
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
        textView_totalMark.setText("Mark:" + (int) getAverageMark(markList) + "%");

        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getProjectName() + "</h1>" +
                        "<hr>" +
                        "<p>" + "Group: " + indexOfGroup + "</p >" + "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getProjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + getAverageMark(markList) + "%</p >" +
                        "<h2 style=\"font-weight: normal\">Marker</h2>" + "<p>";
        for (int i = 0; i < project.getAssistant().size(); i++) {
            htmlString = htmlString + project.getAssistant().get(i) + "<br>";
        }
        htmlString += "<h2 style=\"font-weight: normal\">Students</h2>" + "<p>";
        for (int i = 0; i < studentInfoArrayList.size(); i++)
            htmlString = htmlString + studentInfoArrayList.get(i).getNumber() + "---" + studentInfoArrayList.get(i).getFirstName() + " " + studentInfoArrayList.get(i).getMiddleName() + " " + studentInfoArrayList.get(i).getSurname() + "<br>";

        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\">MarkedCriteria</h2>" + "<p>";
        for (int i = 0; i < markList.get(0).getCriteriaList().size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + markList.get(0).getCriteriaList().get(i).getName() + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + getAverageCriterionMark(markList, i) + "/" + Double.valueOf(markList.get(0).getCriteriaList().get(i).getMaximunMark()) + "</span></h3>";
            for (int j = 0; j < markList.size(); j++) {
                htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + markList.get(j).getLecturerName() + ":</h4>";
                if (markList.get(j).getCriteriaList().size() > 0)
                    for (int k = 0; k < markList.get(j).getCriteriaList().get(i).getSubsectionList().size(); k++) {
                        htmlString += "<p>" + markList.get(j).getCriteriaList().get(i).getSubsectionList().get(k).getName() + " : "
                                + markList.get(j).getCriteriaList().get(i).getSubsectionList().get(k).getShortTextList().get(0).getLongtext() + "</p >";
                    }
            }
            htmlString += "<br>";
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_sendReportGroup);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }

    public double getAverageCriterionMark(ArrayList<Mark> markList, int criteriaIndex) {
        double sumMark = 0;
        double markers = markList.size();
        for (int i = 0; i < markers; i++) {
            if (markList.get(i).getMarkList().size() == 0) {
                return markList.get(0).getMarkList().get(criteriaIndex);
            } else {
                sumMark += markList.get(i).getMarkList().get(criteriaIndex);
            }
        }
        Log.d("EEEE", "sum of mark: " + sumMark);
        double avgMark = sumMark/markers;
        Log.d("EEEE", "avg mark: " + avgMark);
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.d("EEEE", "avg mark: " + avgMark);
        return avgMark;
    }

    public double getAverageMark(ArrayList<Mark> markList) {
        double sumMark = 0;
        double markers = markList.size();
        for (int i = 0; i < markers; i++) {
            if (markList.get(i).getTotalMark() == -999) {
                return markList.get(0).getTotalMark();
            } else {
                sumMark += markList.get(i).getTotalMark();
            }
        }
        Log.d("EEEE", "sum of mark: " + sumMark);
        double avgMark = sumMark/markers;
        Log.d("EEEE", "avg mark: " + avgMark);
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.d("EEEE", "avg mark: " + avgMark);
        return avgMark;
    }
}

