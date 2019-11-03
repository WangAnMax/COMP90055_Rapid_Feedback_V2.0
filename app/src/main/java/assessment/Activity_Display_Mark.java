/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedback.Activity_Login;
import com.example.feedback.R;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Assessment;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Remark;
import newdbclass.SelectedComment;

public class Activity_Display_Mark extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfStudent;
    private int indexOfGroup;
    private Remark remark;
    private Handler handler;
    private Toolbar mToolbar;
    private Project project;
    private ProjectStudent student;
    private String from;
    public static final String FROMREALTIME = "realtime";
    public static final String FROMREVIEW = "review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mark);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        bindHandler();
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentList().get(indexOfStudent);
        init();
        for (int i = 0; i < student.getRemarkList().size(); i++) {
            if (student.getRemarkList().get(i).getId() == AllFunctions.getObject().getId()) {
                remark = student.getRemarkList().get(i);
                break;
            }
        }
        AllFunctions.getObject().setHandler(handler);
    }

    public void bindHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_Display_Mark.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        init();
                        break;
                    case 109:
                        Toast.makeText(Activity_Display_Mark.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    public void onNewIntent(Intent intent) {
        initToolbar();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        bindHandler();
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentList().get(indexOfStudent);
        for (int i = 0; i < student.getRemarkList().size(); i++) {
            if (student.getRemarkList().get(i).getId() == AllFunctions.getObject().getId()) {
                remark = student.getRemarkList().get(i);
                break;
            }
        }
        AllFunctions.getObject().setHandler(handler);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_reaper_mark);
        mToolbar.setTitle("Mark -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (from.equals(Activity_Assessment.FROMREALTIME)
                        || from.equals(Activity_Send_Report_Individual.FROMREALTIMESEND)) {
                    Intent intent = new Intent(Activity_Display_Mark.this, Activity_Realtime_Assessment.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Assessment.FROMREVIEW)
                        || from.equals(Activity_Send_Report_Individual.FROMREVIEWSEND)) {
                    Intent intent = new Intent(Activity_Display_Mark.this, Activity_Review_Report.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Display_Mark.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Display_Mark.this,
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

    private void init() {
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentList().get(indexOfStudent);

        for (int n = 0; n < project.getMarkerList().size(); n++) {
            boolean hasRemark = false;
            for (int m = 0; m < student.getRemarkList().size(); m++) {
                if (project.getMarkerList().get(n).getId() == student.getRemarkList().get(m).getId()) {
                    hasRemark = true;
                }
            }

            if (!hasRemark) {
                Remark remark = new Remark();
                remark.setId(project.getMarkerList().get(n).getId());
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
                student.getRemarkList().add(remark);
            }
        }

        MyAdapterForGridView myAdapterForGridView = new MyAdapterForGridView(student.getRemarkList(), this);
        GridView gridViewMark = findViewById(R.id.listView_markItem_markPage);
        gridViewMark.setAdapter(myAdapterForGridView);
    }

    public void refreshMarkPage(View v) {
        AllFunctions.getObject().setHandler(handler);
        AllFunctions.getObject().syncProjectList();
    }


    public class MyAdapterForGridView extends BaseAdapter {

        private Context mContext;
        private ArrayList<Remark> remarkList;

        public MyAdapterForGridView(ArrayList<Remark> remarkList, Context context) {
            this.remarkList = remarkList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return remarkList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_mark_markpage, parent, false);

            TextView textView_totalMark = convertView.findViewById(R.id.textView_totalMark_gridItemMark);
            if (getTotalMark(remarkList.get(position)) >= 0) {
                textView_totalMark.setText(String.format("%.2f", getTotalMark(remarkList.get(position))) + "%");
            } else {
                textView_totalMark.setText("Marking...");
            }

            TextView textView_assessorName = convertView.findViewById(R.id.textView_assessorName_gridItemMark);
            String lecturerName = "";
            for (int i = 0; i < project.getMarkerList().size(); i++) {
                if (project.getMarkerList().get(i).getId() == remarkList.get(position).getId()) {
                    lecturerName = project.getMarkerList().get(i).getFirstName() + " "
                            + project.getMarkerList().get(i).getMiddleName() + " "
                            + project.getMarkerList().get(i).getLastName();
                }
            }
            textView_assessorName.setText(lecturerName + "");

            ListView listView_gridCriteria = convertView.findViewById(R.id.listView_criteriaMark_gridItemMark);
            MyAdapterForGridItem myAdapterForGridItem = new MyAdapterForGridItem(remarkList.get(position), convertView.getContext());
            listView_gridCriteria.setAdapter(myAdapterForGridItem);
            setListViewHeightBasedOnChildren(listView_gridCriteria);

            Button button_viewReport = convertView.findViewById(R.id.button_viewReport_gridItemMark);
            button_viewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (from.equals(Activity_Assessment.FROMREALTIME) || from.equals(Activity_Send_Report_Individual.FROMREALTIMESEND)) {
                        if (indexOfGroup == 0) {
                            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Editable_Individual_Report.class);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("indexOfMark", String.valueOf(position));
                            intent.putExtra("from", FROMREALTIME);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Editable_Group_Report.class);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("indexOfMark", String.valueOf(position));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("from", FROMREALTIME);
                            startActivity(intent);
                        }
                    } else if (from.equals(FROMREVIEW) || from.equals(Activity_Send_Report_Individual.FROMREVIEWSEND)) {
                        if (indexOfGroup == 0) {
                            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Editable_Individual_Report.class);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("indexOfMark", String.valueOf(position));
                            intent.putExtra("from", FROMREVIEW);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Editable_Group_Report.class);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("indexOfMark", String.valueOf(position));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("from", FROMREVIEW);
                            startActivity(intent);
                        }
                    }
                }
            });

            if (getTotalMark(remarkList.get(position)) < 0
                    || remarkList.get(position).getAssessmentList().size() == 0) {
                button_viewReport.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
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


    public class MyAdapterForGridItem extends BaseAdapter {
        private Context mContext;
        private Remark remarkItem;

        public MyAdapterForGridItem(Remark remarkItem, Context context) {
            this.remarkItem = remarkItem;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return remarkItem.getAssessmentList().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_criteria_andmark, parent, false);

            TextView textView_markWithTotalMark = convertView.findViewById(R.id.textView_markTotalMark_listItemCriteriaMark);
            double maximumMark = 0;
            String criterionName = "";
            for (int i = 0; i < project.getCriterionList().size(); i++) {
                if (project.getCriterionList().get(i).getId() == remarkItem.getAssessmentList().get(position).getCriterionId()) {
                    maximumMark = project.getCriterionList().get(i).getMaximumMark();
                    criterionName = project.getCriterionList().get(i).getName();
                }
            }

            if (remarkItem.getAssessmentList().get(position).getScore() == -1) {
                textView_markWithTotalMark.setText("0/" + Double.valueOf(maximumMark));
            } else {
                textView_markWithTotalMark.setText(remarkItem.getAssessmentList().get(position).getScore() + "/" + Double.valueOf(maximumMark));
            }
            TextView textView_criteriaName = convertView.findViewById(R.id.textView_criteriaName_listItemCriteriaMark);
            textView_criteriaName.setText(criterionName);
            convertView.setEnabled(false);

            return convertView;
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void onBackPressed() {
        if (from.equals(Activity_Assessment.FROMREALTIME)
                || from.equals(Activity_Send_Report_Individual.FROMREALTIMESEND)) {
            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Realtime_Assessment.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (from.equals(Activity_Assessment.FROMREVIEW)
                || from.equals(Activity_Send_Report_Individual.FROMREVIEWSEND)) {
            Intent intent = new Intent(Activity_Display_Mark.this, Activity_Review_Report.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
