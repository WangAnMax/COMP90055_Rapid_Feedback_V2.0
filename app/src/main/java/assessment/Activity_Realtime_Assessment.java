/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedback.Activity_Homepage;
import com.example.feedback.Activity_Login;
import com.example.feedback.R;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Remark;
import newdbclass.Student;

public class Activity_Realtime_Assessment extends AppCompatActivity {
    private Toolbar mToolbar;
    private ListView listView_projects;
    private ListView listView_students;
    private ArrayList<Project> projectList;
    private int indexOfProject;
    private MyAdapter myAdapter;
    private TextView textView_duration_title;
    private TextView textView_numCandicateAndMarker;
    public static final String FROMREALTIME = "realtime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_assessment_page);
        initToolbar();
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        myAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_realtime_assessment);
        mToolbar.setTitle("Assessment -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Realtime_Assessment.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Realtime_Assessment.this,
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void init() {
        projectList = AllFunctions.getObject().getProjectList();
        MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                (Activity_Realtime_Assessment.this, projectList);
        listView_projects = findViewById(R.id.listView_projects_realtimeAssessment);
        listView_students = findViewById(R.id.listView_students_realtimeAssessment);
        listView_projects.setAdapter(myAdapterDefaultlistView);
        listView_projects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++)
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(getResources().getColor(R.color.check));
                indexOfProject = position;
                Project project = projectList.get(position);
                myAdapter = new MyAdapter(project.getStudentList(), Activity_Realtime_Assessment.this);
                listView_students.setAdapter(myAdapter);
                textView_duration_title = findViewById(R.id.textView_duration_realtimeAssessment);
                textView_duration_title.setText("Presentation duration: " + projectList.get(indexOfProject).getDurationMin() + ":" +
                        +projectList.get(indexOfProject).getDurationSec());
                textView_numCandicateAndMarker = findViewById(R.id.textView_numCandidatesMarkers_realtimeAssessment);
                int numStudentHasMarked = 0;
                for (int i = 0; i < projectList.get(indexOfProject).getStudentList().size(); i++) {
                    if (projectList.get(indexOfProject).getStudentList().get(i).getFinalScore() > 0.0)
                        numStudentHasMarked++;
                }
                textView_numCandicateAndMarker.setText(numStudentHasMarked + " of " +
                        projectList.get(indexOfProject).getStudentList().size() + " candidate(s) marked by " +
                        projectList.get(indexOfProject).getMarkerList().size() + " marker(s)");
            }
        });
    }

    private void resetStudentListView() {
        listView_students.setAdapter(null);
        textView_duration_title.setText("");
        textView_numCandicateAndMarker.setText("");
    }


    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<ProjectStudent> studentList;

        public MyAdapter(ArrayList<ProjectStudent> studentList, Context context) {
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_student_withbutton, parent, false);

            TextView textView_groupNum = convertView.findViewById(R.id.textView_group_studentswithButton);
            if (studentList.get(position).getGroupNumber() == 0)
                textView_groupNum.setText("");
            else
                textView_groupNum.setText(String.valueOf(studentList.get(position).getGroupNumber()));
            TextView textView_studentID = convertView.findViewById(R.id.textView_studentID_studentsWithButton);
            textView_studentID.setText(studentList.get(position).getStudentNumber() + "");
            TextView textView_studentName = convertView.findViewById(R.id.textView_fullname_studentsWithButton);
            textView_studentName.setText(studentList.get(position).getFirstName() + " " + studentList.get(position).getMiddleName() + " " + studentList.get(position).getLastName());
            TextView textView_studentEmail = convertView.findViewById(R.id.textView_email_studentsWithButton);
            textView_studentEmail.setText(studentList.get(position).getEmail());
            Button button_start = convertView.findViewById(R.id.button_start_studentsWithButton);
            button_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfStudent", String.valueOf(position));
                    intent.putExtra("indexOfGroup", String.valueOf(studentList.get(position).getGroupNumber()));
                    intent.putExtra("from", FROMREALTIME);
                    startActivity(intent);
                }
            });
            ArrayList<Remark> remarkList = studentList.get(position).getRemarkList();
            for (int i = 0; i < remarkList.size(); i++) {
                if (remarkList.get(i).getId() == AllFunctions.getObject().getId()) {
                    button_start.setVisibility(View.INVISIBLE);
                    button_start.setEnabled(false);
                    convertView.setEnabled(false);
                    listView_students.setItemChecked(position, false);
                }
            }

            if (listView_students.isItemChecked(position))
                convertView.setBackgroundColor(Color.parseColor("#D2EBF7"));
            else
                convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }
    }

    public class MyAdapterDefaultlistView extends BaseAdapter {

        private ArrayList<Project> mProjectList;
        private Context mContext;

        public MyAdapterDefaultlistView(Context context, ArrayList<Project> projectList) {
            this.mProjectList = projectList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mProjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_default, parent, false);
            TextView textView_listItem = (TextView) convertView.findViewById(R.id.textView_defaultView);
            textView_listItem.setText(mProjectList.get(position).getName());
            return convertView;
        }
    }

}
