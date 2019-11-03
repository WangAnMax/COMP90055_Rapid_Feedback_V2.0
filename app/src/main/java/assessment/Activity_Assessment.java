/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.feedback.Activity_Login;
import com.example.feedback.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Assessment;
import newdbclass.Criterion;
import newdbclass.Project;
import newdbclass.Remark;
import newdbclass.SelectedComment;

public class Activity_Assessment extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    MyAdapter myAdapter;
    MyAdapter3 myAdapter3;
    int indexOfProject;
    int indexOfStudent;
    int indexOfGroup;
    ArrayList<Integer> studentList;
    private static Project project;
    ArrayList<Criterion> criteriaList;
    ListView lv_individual;
    ListView lv_otherComment;
    TextView tv_time;
    Button btn_assessment_start;
    Button btn_assessment_refresh;
    TextView tv_assessment_student;
    TextView tv_assessment_total_mark;
    SeekBar sb_mark;
    TextView tv_mark;
    Double totalMark = 0.0;
    int totalWeighting = 0;
    EditText et_other_comment;
    private long durationTime = 0;
    private long warningTime = 0;
    private boolean isPause = false;
    private CountDownTimer countDownTimer;
    private long leftTime = 0;
    private int flag = 0;
    static private int matrixOfMarkedCriteria[][];
    static private int matrixCriteriaLongtext[][];
    private AllFunctions allFunctions;
    private Handler handler;
    private AlertDialog dialog;
    private String from;
    public static final String FROMREALTIME = "realtime";
    public static final String FROMREVIEW = "review";
    private Remark remark = null;
    private boolean goBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);

        tv_assessment_student = findViewById(R.id.tv_assessment_student);
        studentList = new ArrayList<>();

        if (indexOfGroup == 0) {
            tv_assessment_student.setText(project.getStudentList().get(indexOfStudent).getStudentNumber() + " --- " +
                    project.getStudentList().get(indexOfStudent).getFirstName() + " " +
                    project.getStudentList().get(indexOfStudent).getMiddleName() + " " +
                    project.getStudentList().get(indexOfStudent).getLastName());
            studentList.add(indexOfStudent);
        } else {
            tv_assessment_student.setText("Group " + indexOfGroup);
            for (int i = 0; i < project.getStudentList().size(); i++) {
                if (project.getStudentList().get(i).getGroupNumber() == indexOfGroup) {
                    studentList.add(i);
                }
            }
        }

        tv_assessment_total_mark = findViewById(R.id.tv_assessment_total_mark);

        ArrayList<Remark> remarkList = project.getStudentList().get(studentList.get(0)).getRemarkList();
        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == AllFunctions.getObject().getId()) {
                remark = remarkList.get(i);
                break;
            }
        }

        if (remark != null) {
            markObjectToMatrix(remark);
            for (int m = 0; m < studentList.size(); m++) {
                for (int n = 0; n < project.getCriterionList().size(); n++) {
                    int criterionId = project.getCriterionList().get(n).getId();
                    Assessment assessment = new Assessment();
                    for (int a = 0; a < remark.getAssessmentList().size(); a++) {
                        if (remark.getAssessmentList().get(a).getCriterionId() == criterionId) {
                            assessment = remark.getAssessmentList().get(a);
                            break;
                        }
                    }
                    SelectedComment selectedComment = new SelectedComment();
                    for (int k = 0; k < project.getCriterionList().get(n).getFieldList().size(); k++) {

                        int fieldId = project.getCriterionList().get(n).getFieldList().get(k).getId();
                        for (int b = 0; b < assessment.getSelectedCommentList().size(); b++) {
                            if (assessment.getSelectedCommentList().get(b).getFieldId() == fieldId) {
                                selectedComment = assessment.getSelectedCommentList().get(b);
                            }
                        }
                    }
                    selectedComment.setExCommentId(-999);
                }
            }

            double totalWeight = 0;
            for (int j = 0; j < project.getCriterionList().size(); j++) {
                totalWeight = totalWeighting + project.getCriterionList().get(j).getMaximumMark();
            }
            totalWeighting = Double.valueOf(totalWeight).intValue();

            for (int k = 0; k < project.getCriterionList().size(); k++) {
                totalMark = remark.getAssessmentList().get(k).getScore();
            }

            if (project.getStudentList().get(studentList.get(0)).getFinalScore() == -1) {
                tv_assessment_total_mark.setText("0%");
            } else {
                tv_assessment_total_mark.setText(String.format("%.2f", project.getStudentList().get(studentList.get(0)).getFinalScore()) + "%");
            }
        } else {
            initMatrix();

            tv_assessment_total_mark.setText("0%");
            for (int m = 0; m < studentList.size(); m++) {
                Remark remark = new Remark();
                remark.setId(AllFunctions.getObject().getId());
                ArrayList<Assessment> assessmentList = new ArrayList<>();
                for (int i = 0; i < project.getCriterionList().size(); i++) {
                    Assessment assessment = new Assessment();
                    assessment.setCriterionId(project.getCriterionList().get(i).getId());
                    assessment.setScore(-1);
                    ArrayList<SelectedComment> selectedCommentList = new ArrayList<>();
                    for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                        SelectedComment selectedComment = new SelectedComment();
                        selectedComment.setFieldId(project.getCriterionList().get(i).getFieldList().get(j).getId());
                        selectedCommentList.add(selectedComment);
                    }
                    assessment.setSelectedCommentList(selectedCommentList);
                    assessmentList.add(assessment);
                }
                remark.setAssessmentList(assessmentList);
                project.getStudentList().get(studentList.get(m)).getRemarkList().add(remark);
            }

            for (int a = 0; a < project.getStudentList().get(studentList.get(0)).getRemarkList().size(); a++) {
                if (project.getStudentList().get(studentList.get(0)).getRemarkList().get(a).getId() == AllFunctions.getObject().getId()) {
                    remark = project.getStudentList().get(studentList.get(0)).getRemarkList().get(a);
                    break;
                }
            }

            double totalWeight = 0;
            for (int j = 0; j < project.getCriterionList().size(); j++) {
                totalWeight = totalWeight + project.getCriterionList().get(j).getMaximumMark();
            }
            totalWeighting = Double.valueOf(totalWeight).intValue();
        }

        lv_individual = findViewById(R.id.lv_individual);
        lv_otherComment = findViewById(R.id.lv_otherComment);
        init();

        tv_time = findViewById(R.id.tv_time);
        tv_time.setText(String.format("%02d", durationTime / 1000 / 60) + ":" + String.format("%02d", durationTime / 1000 % 60));

        btn_assessment_start = findViewById(R.id.btn_assessment_start);
        btn_assessment_refresh = findViewById(R.id.btn_assessment_refresh);

        findViewById(R.id.btn_assessment_start).setOnClickListener(this);
        findViewById(R.id.btn_assessment_refresh).setOnClickListener(this);

        btn_assessment_start.setEnabled(true);
        btn_assessment_refresh.setEnabled(false);
        initTimer(durationTime);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        allFunctions = AllFunctions.getObject();
        allFunctions.setHandler(handler);
        lv_individual.setAdapter(myAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_assessment);
        mToolbar.setTitle("Assessment -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goBack = true;
                allFunctions.syncProjectList();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Assessment.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Assessment.this,
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

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_Assessment.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        if (goBack) {
                            if (from.equals(FROMREALTIME)) {
                                finish();
                            }
                        } else {
                            if (from.equals(Activity_Realtime_Assessment.FROMREALTIME) && !goBack) {
                                Intent intent = new Intent(Activity_Assessment.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                                intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                                intent.putExtra("from", FROMREALTIME);
                                startActivity(intent);
                                finish();
                            }
                        }
                        break;
                    case 109:
                        Toast.makeText(Activity_Assessment.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    case 128:
                        Toast.makeText(Activity_Assessment.this,
                                "Record mark success", Toast.LENGTH_SHORT).show();
                        AllFunctions.getObject().syncProjectList();
                        break;
                    case 129:
                        Toast.makeText(Activity_Assessment.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        allFunctions = AllFunctions.getObject();
        allFunctions.setHandler(handler);
        criteriaList = project.getCriterionList();
        durationTime = project.getDurationMin() * 60000 + project.getDurationSec() * 1000;
        warningTime = project.getWarningMin() * 60000 + project.getWarningSec() * 1000;
        myAdapter = new MyAdapter(criteriaList, this);
        myAdapter3 = new MyAdapter3(studentList, this);

        lv_individual.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(lv_individual);
        lv_otherComment.setAdapter(myAdapter3);
        setListViewHeightBasedOnChildren(lv_otherComment);
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Criterion> criteriaList;
        private Double increment = 0.0;

        public MyAdapter(ArrayList<Criterion> criteriaList, Context context) {
            this.criteriaList = criteriaList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return criteriaList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_individual_assessment, parent, false);
            final View view10 = convertView;
            TextView tv_criteria_name = convertView.findViewById(R.id.tv_criteria_name);
            tv_criteria_name.setText(project.getCriterionList().get(position).getName());

            if (project.getCriterionList().get(position).getMarkIncrement() == 1) {
                increment = 1.0;
            } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.5) {
                increment = 0.5;
            } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.25) {
                increment = 0.25;
            }

            TextView tv_red = view10.findViewById(R.id.tv_red);
            TextView tv_yellow = view10.findViewById(R.id.tv_yellow);
            TextView tv_green = view10.findViewById(R.id.tv_green);

            ArrayList<Integer> weightList = new ArrayList<>();

            weightList.add(0, 0);
            weightList.add(1, 0);
            weightList.add(2, 0);


            if (getMatrixMarkedCriteria(position).size() != 0) {
                for (int i = 0; i < getMatrixMarkedCriteria(position).size(); i++) {

                    int j = getMatrixMarkedCriteria(position).get(i).get(0);
                    int m = getMatrixMarkedCriteria(position).get(i).get(1);

                    String type = project.getCriterionList().get(position).getFieldList().get(j).getCommentList().get(m).getType();
                    if (type.equals("negative")) {
                        weightList.set(0, (weightList.get(0) + 1));
                    } else if (type.equals("neutral")) {
                        weightList.set(1, (weightList.get(1) + 1));
                    } else if (type.equals("positive")) {
                        weightList.set(2, (weightList.get(2) + 1));
                    }
                }
            }

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(0));

            tv_red.setLayoutParams(param1);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(1));

            tv_yellow.setLayoutParams(param2);

            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(2));

            tv_green.setLayoutParams(param3);

            Button btn_assessment_comment = convertView.findViewById(R.id.btn_assessment_comment_back);
            btn_assessment_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Activity_Assessment.this, Activity_Assessment_Comment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfCriteria", String.valueOf(position));
                    intent.putExtra("indexOfComment", String.valueOf(-999));

                    startActivity(intent);
                }
            });

            sb_mark = convertView.findViewById(R.id.sb_mark);
            tv_mark = convertView.findViewById(R.id.tv_mark);
            sb_mark.setMax((int) (project.getCriterionList().get(position).getMaximumMark() / increment));
            final View view2 = convertView;

            int criterionId = project.getCriterionList().get(position).getId();
            Assessment assessment = new Assessment();
            for (int a = 0; a < remark.getAssessmentList().size(); a++) {
                if (remark.getAssessmentList().get(a).getCriterionId() == criterionId) {
                    assessment = remark.getAssessmentList().get(a);
                    break;
                }
            }
            sb_mark.setProgress(Double.valueOf(assessment.getScore() / increment).intValue());
            if (assessment.getScore() == -1) {
                tv_mark.setText("0 / " + Double.valueOf(project.getCriterionList().get(position).getMaximumMark()));
            } else {
                tv_mark.setText(assessment.getScore() + " / " + Double.valueOf(project.getCriterionList().get(position).getMaximumMark()));
            }

            sb_mark.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (project.getCriterionList().get(position).getMarkIncrement() == 1) {
                        increment = 1.0;
                    } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.5) {
                        increment = 0.5;
                    } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.25) {
                        increment = 0.25;
                    }

                    Double progressDisplay = progress * increment;
                    tv_mark = view2.findViewById(R.id.tv_mark);
                    tv_mark.setText(String.valueOf(progressDisplay) + " / " + project.getCriterionList().get(position).getMaximumMark());

                    for (int i = 0; i < studentList.size(); i++) {
                        Remark newRemark = new Remark();
                        ArrayList<Remark> remarkList = project.getStudentList().get(studentList.get(i)).getRemarkList();
                        for (int j = 0; j < remarkList.size(); j++) {
                            if (remarkList.get(j).getId() == AllFunctions.getObject().getId()) {
                                newRemark = remarkList.get(j);
                            }
                        }

                        int criterionId = project.getCriterionList().get(position).getId();
                        Assessment assessment = new Assessment();
                        for (int a = 0; a < newRemark.getAssessmentList().size(); a++) {
                            if (newRemark.getAssessmentList().get(a).getCriterionId() == criterionId) {
                                assessment = newRemark.getAssessmentList().get(a);
                                break;
                            }
                        }

                        assessment.setScore(progressDisplay);
                    }

                    double totalMark = totalMark();
                    tv_assessment_total_mark.setText(String.format("%.2f", totalMark) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return convertView;
        }

    }

    public double totalMark() {
        double totalMark = 0;
        for (int i = 0; i < studentList.size(); i++) {
            Double sum = 0.0;

            Remark newRemark = new Remark();
            ArrayList<Remark> remarkList = project.getStudentList().get(studentList.get(i)).getRemarkList();
            for (int j = 0; j < remarkList.size(); j++) {
                if (remarkList.get(j).getId() == AllFunctions.getObject().getId()) {
                    newRemark = remarkList.get(j);
                }
            }
            for (int m = 0; m < newRemark.getAssessmentList().size(); m++) {
                sum = sum + newRemark.getAssessmentList().get(m).getScore();
            }
            totalMark = sum * (100.0 / totalWeighting);
        }
        return totalMark;
    }


    public void initTimer(long millisUntilFinished) {

        btn_assessment_start.setEnabled(true);

        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                if (leftTime < warningTime) {
                    tv_time.setTextColor(android.graphics.Color.RED);
                }
                tv_time.setText(String.format("%02d", millisUntilFinished / 1000 / 60) + ":" + String.format("%02d", millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                tv_time.setText("00:00");
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_assessment_start:
                if (flag == 0) {
                    isPause = false;
                    countDownTimer.start();
                    btn_assessment_start.setBackgroundResource(R.drawable.ic_pause);
                    flag = 1;
                    btn_assessment_refresh.setEnabled(false);
                    break;
                } else if (flag == 1) {
                    if (!isPause) {
                        isPause = true;
                        countDownTimer.cancel();
                    }

                    btn_assessment_start.setBackgroundResource(R.drawable.ic_start);
                    flag = 2;
                    btn_assessment_refresh.setEnabled(true);
                    break;

                } else {
                    if (leftTime != 0 && isPause) {
                        countDownTimer.start();
                        isPause = false;

                    }
                    btn_assessment_refresh.setEnabled(false);
                    btn_assessment_start.setBackgroundResource(R.drawable.ic_pause);

                    flag = 1;
                    break;
                }

            case R.id.btn_assessment_refresh:
                countDownTimer.cancel();
                btn_assessment_refresh.setEnabled(false);
                tv_time.setText(String.format("%02d", durationTime / 1000 / 60) + ":" + String.format("%02d", durationTime / 1000 % 60));
                initTimer(durationTime);
                flag = 0;
                break;
            default:
                break;
        }
    }

    public class MyAdapter3 extends BaseAdapter {

        private Context mContext;
        private ArrayList<Integer> studentList;

        public MyAdapter3(ArrayList<Integer> studentList,
                          Context context) {
            this.studentList = studentList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return studentList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_other_comment, parent, false);

            TextView tv_other_comment = convertView.findViewById(R.id.tv_other_comment);
            tv_other_comment.setText("For " + project.getStudentList().get(studentList.get(position)).getFirstName() + " "
                    + project.getStudentList().get(studentList.get(position)).getMiddleName() + " "
                    + project.getStudentList().get(studentList.get(position)).getLastName());
            Button btn_assessment_save = convertView.findViewById(R.id.btn_assessment_save);
            et_other_comment = convertView.findViewById(R.id.et_other_comment);

            if (project.getStudentList().get(studentList.get(position)).getFinalRemark() != null) {
                et_other_comment.setText(getRemark(project.getStudentList().get(studentList.get(position)).getRemarkList()).getText());
            }
            final View view4 = convertView;
            btn_assessment_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    et_other_comment = view4.findViewById(R.id.et_other_comment);
                    String otherComment = et_other_comment.getText().toString();
                    getRemark(project.getStudentList().get(studentList.get(position)).getRemarkList()).setText(otherComment);
                }
            });
            return convertView;
        }
    }

    public Remark getRemark(ArrayList<Remark> remarkList) {
        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == AllFunctions.getObject().getId()) {
                return remarkList.get(i);
            }
        }
        return null;
    }

    public void finishAssessment(View view) {

        if (checkAllCriteria()) {
            addSubsectionToMarkObject();
            setAssessmentScores(remark);

            for (int i = 0; i < studentList.size(); i++) {
                int studentId = project.getStudentList().get(studentList.get(i)).getId();
                Remark newRemark = getRemark(project.getStudentList().get(studentList.get(i)).getRemarkList());
                remark.setText(newRemark.getText());
                goBack = false;
                AllFunctions.getObject().sendMark(project.getId(), studentId, JSON.toJSONString(remark));
            }
        } else {
            Toast.makeText(this, "You have one or more comments not selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAssessmentScores(Remark remark) {
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            if (remark.getAssessmentList().get(i).getScore() == -1) {
                remark.getAssessmentList().get(i).setScore(0);
            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private void initMatrix() {
        matrixOfMarkedCriteria = new int[project.getCriterionList().size()][10];
        matrixCriteriaLongtext = new int[project.getCriterionList().size()][10];
        for (int i = 0; i < project.getCriterionList().size(); i++)
            for (int j = 0; j < 10; j++)
                matrixOfMarkedCriteria[i][j] = -999;
    }

    static public void saveCommentToMatrixCriteria(int criteriaIndex, int subsectionIndex, int shortIndex, int longIndex) {
        matrixOfMarkedCriteria[criteriaIndex][subsectionIndex] = shortIndex;
        matrixCriteriaLongtext[criteriaIndex][subsectionIndex] = longIndex;
    }

    static public ArrayList<ArrayList<Integer>> getMatrixMarkedCriteria(int criteriaIndex) {
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < project.getCriterionList().get(criteriaIndex).getFieldList().size(); i++) {
            if (matrixOfMarkedCriteria[criteriaIndex][i] != -999) {
                ArrayList<Integer> arrayList_ls = new ArrayList<Integer>();
                arrayList_ls.add(i);
                arrayList_ls.add(matrixOfMarkedCriteria[criteriaIndex][i]);
                arrayList_ls.add(matrixCriteriaLongtext[criteriaIndex][i]);
                arrayLists.add(arrayList_ls);
            }
        }
        return arrayLists;
    }

    static public boolean markedCriteriaSelectedAll(int criteriaIndex) {
        for (int i = 0; i < project.getCriterionList().get(criteriaIndex).getFieldList().size(); i++) {
            if (matrixOfMarkedCriteria[criteriaIndex][i] == -999)
                return false;
        }
        return true;
    }

    private void addSubsectionToMarkObject() {
        ArrayList<Criterion> criterionList = project.getCriterionList();
        for (int i = 0; i < studentList.size(); i++) {
            for (int j = 0; j < criterionList.size(); j++) {

                int criterionId = criterionList.get(j).getId();
                Assessment assessment = new Assessment();
                for (int a = 0; a < remark.getAssessmentList().size(); a++) {
                    if (remark.getAssessmentList().get(a).getCriterionId() == criterionId) {
                        assessment = remark.getAssessmentList().get(a);
                        break;
                    }
                }
                for (int k = 0; k < project.getCriterionList().get(j).getFieldList().size(); k++) {

                    int fieldId = project.getCriterionList().get(j).getFieldList().get(k).getId();
                    SelectedComment selectedComment = new SelectedComment();
                    for (int b = 0; b < assessment.getSelectedCommentList().size(); b++) {
                        if (assessment.getSelectedCommentList().get(b).getFieldId() == fieldId) {
                            selectedComment = assessment.getSelectedCommentList().get(b);
                        }
                    }

                    int exCommentId = project.getCriterionList().get(j).getFieldList().get(k).getCommentList().get(matrixOfMarkedCriteria[j][k]).getExpandedCommentList().get(matrixCriteriaLongtext[j][k]).getId();
                    selectedComment.setExCommentId(exCommentId);
                }
            }
        }
    }

    private boolean checkAllCriteria() {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (matrixOfMarkedCriteria[i][0] == -999)
                return false;
        }
        return true;
    }

    private void markObjectToMatrix(Remark remark) {
        initMatrix();
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            //criteria layer
            int criterionId = project.getCriterionList().get(i).getId();
            Assessment assessment = new Assessment();
            for (int a = 0; a < remark.getAssessmentList().size(); a++) {
                if (remark.getAssessmentList().get(a).getCriterionId() == criterionId) {
                    assessment = remark.getAssessmentList().get(a);
                    break;
                }
            }
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                //field layer
                int fieldId = project.getCriterionList().get(i).getFieldList().get(j).getId();
                SelectedComment selectedComment = new SelectedComment();
                for (int b = 0; b < assessment.getSelectedCommentList().size(); b++) {
                    if (assessment.getSelectedCommentList().get(b).getFieldId() == fieldId) {
                        selectedComment = assessment.getSelectedCommentList().get(b);
                        break;
                    }
                }
                for (int k = 0; k < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().size(); k++) {
                    //comment layer
                    for (int p = 0; p < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().size(); p++) {
                        int exCommentId = project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().get(p).getId();
                        if (selectedComment.getExCommentId() == exCommentId) {
                            matrixOfMarkedCriteria[i][j] = k;
                            matrixCriteriaLongtext[i][j] = p;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        goBack = true;
        allFunctions.syncProjectList();
    }
}
