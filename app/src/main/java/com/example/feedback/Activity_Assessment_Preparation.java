/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Criterion;
import newdbclass.Marker;
import newdbclass.Project;

public class Activity_Assessment_Preparation extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private AllFunctions allFunctions = AllFunctions.getObject();
    private ArrayList<Project> projectList;
    private MyAdapterForListView myAdapter;
    private int indexToSend = -999;
    private Project project;
    private int projectId;
    private Handler handler;
    private Toolbar mToolbar;
    private CheckBox mDeleteCheckbox;
    private Button button_about;
    private Button button_criteria;
    private Button button_student;
    private Button button_assessor;
    private Button button_project_add;
    private Button button_sync_projectlist;
    private TextView textView_projectName;
    private TextView textView_aboutDetail;
    private TextView textView_criteriaDetail;
    private TextView textView_asseccorDetail;
    private TextView student_detail__inpreparation;
    private EditText editText_assessorName;
    private AlertDialog dialog;
    private MyAdapterDefaultlistView myAdapterDefaultlistView;
    public static final String FROMPREVIOUSPROJECT = "old";
    public static final String FROMNEWPROJECT = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_preparation_);
        init();
    }

    protected void onNewIntent(Intent intent) {
        bindHandler();
        myAdapterDefaultlistView.notifyDataSetChanged();
        if (indexToSend == -999) {
            init();
        } else {
            showOtherInfo(indexToSend);
        }
    }

    private void init() {
        bindHandler();
        textView_projectName = findViewById(R.id.project_name_inpreparation);
        textView_aboutDetail = findViewById(R.id.about_detail_inpreparation);
        textView_criteriaDetail = findViewById(R.id.criteria_detail__inpreparation);
        textView_asseccorDetail = findViewById(R.id.asseccor_detail__inpreparation);
        student_detail__inpreparation = findViewById(R.id.student_detail__inpreparation);
        button_about = findViewById(R.id.button_about_inpreparation);
        button_criteria = findViewById(R.id.button_criteria_inpreparation);
        button_student = findViewById(R.id.button_studentmanagement__inpreparation);
        button_assessor = findViewById(R.id.button_asseccor_inpreparation);
        button_sync_projectlist = findViewById(R.id.button_sync_projectlist);
        resetDetailView();
        initToolbar();
        projectList = allFunctions.getProjectList();

        button_project_add = findViewById(R.id.button_plus_inpreparation);
        myAdapterDefaultlistView = new MyAdapterDefaultlistView
                (Activity_Assessment_Preparation.this, projectList);
        listView = findViewById(R.id.listView_inpreparation);
        listView.setAdapter(myAdapterDefaultlistView);
        listView.setOnItemClickListener(this);

        myAdapter = new MyAdapterForListView(projectList, Activity_Assessment_Preparation.this);
        mDeleteCheckbox = findViewById(R.id.cb_delete_project);
        mDeleteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    projectList = allFunctions.getProjectList();
                    myAdapter = new MyAdapterForListView(projectList, Activity_Assessment_Preparation.this);

                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(Activity_Assessment_Preparation.this);
                    button_about.setEnabled(false);
                    button_criteria.setEnabled(false);
                    button_student.setEnabled(false);
                    button_assessor.setEnabled(false);
                    button_project_add.setEnabled(false);
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add_disabled, null);
                    button_project_add.setBackground(drawable);
                    button_sync_projectlist.setEnabled(false);
                    button_sync_projectlist.setBackgroundResource(R.drawable.ic_sync_disabled);
                } else {
                    resetDetailView();
                    myAdapterDefaultlistView = new MyAdapterDefaultlistView
                            (Activity_Assessment_Preparation.this, AllFunctions.getObject().getProjectList());
                    listView = findViewById(R.id.listView_inpreparation);
                    listView.setAdapter(myAdapterDefaultlistView);
                    listView.setOnItemClickListener(Activity_Assessment_Preparation.this);
                    button_project_add.setEnabled(true);
                    button_project_add.setBackgroundResource(R.drawable.ripple_add_project);
                    button_sync_projectlist.setEnabled(true);
                    button_sync_projectlist.setBackgroundResource(R.drawable.ripple_sync_project);
                }
            }
        });
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_assessment_presentation);
        mToolbar.setTitle("Project -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Assessment_Preparation.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Assessment_Preparation.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Assessment_Preparation.this,
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Activity_Assessment_Preparation.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        indexToSend = position;
        for (int i = 0; i < parent.getChildCount(); i++)
            parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        view.setBackgroundColor(Color.parseColor("#dbdbdb"));
        showOtherInfo(position);
        button_about.setEnabled(true);
        button_criteria.setEnabled(true);
        button_student.setEnabled(true);
        button_assessor.setEnabled(true);
    }

    public void showOtherInfo(int index) {
        Project project = allFunctions.getProjectList().get(index);
        textView_projectName = findViewById(R.id.project_name_inpreparation);
        textView_projectName.setText(project.getName());
        textView_aboutDetail = findViewById(R.id.about_detail_inpreparation);
        textView_aboutDetail.setText("Subject Name: " + project.getSubjectName() + "\n" +
                "Subject Code: " + project.getSubjectCode() + "\n" +
                "Description: " + project.getDescription() + "\n" +
                "Assessment Duration: " + project.getDurationMin() + ":" + project.getDurationSec() + "\n" +
                "Warning Time: " + project.getWarningMin() + ":" + project.getWarningSec() + "\n");
        textView_criteriaDetail = findViewById(R.id.criteria_detail__inpreparation);
        String criteriaDetailString = "";
        for (Criterion c : project.getCriterionList()) {
            criteriaDetailString = criteriaDetailString + c.getName() + "\n";
            criteriaDetailString = criteriaDetailString + "Maximum Mark: " + c.getMaximumMark() + "\n";
            criteriaDetailString = criteriaDetailString + "Mark Increments: " + c.getMarkIncrement() + "\n\n";
        }
        textView_criteriaDetail.setText(criteriaDetailString);
        textView_asseccorDetail = findViewById(R.id.asseccor_detail__inpreparation);
        String assessorDetailString = new String();
        for (int i = 0; i < project.getMarkerList().size(); i++) {
            Marker marker = project.getMarkerList().get(i);
            assessorDetailString = assessorDetailString + marker.getId() + " - " + marker.getFirstName()
                    + " " + marker.getMiddleName() + " " + marker.getLastName() + "\n";
        }
        textView_asseccorDetail.setText(assessorDetailString);
        this.project = AllFunctions.getObject().getProjectList().get(index);
        projectId = project.getId();
        if (this.project.getPrincipalId() != AllFunctions.getObject().getId()) {
            button_about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Activity_Assessment_Preparation.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_criteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Activity_Assessment_Preparation.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_assessor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Activity_Assessment_Preparation.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Activity_Assessment_Preparation.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            button_about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aboutAssessmentPreparation(view);
                }
            });
            button_criteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    criteriaManagementAssessmentPreparation(view);
                }
            });
            button_assessor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    markerManagementAssessmentPreparation(view);
                }
            });
            button_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studentManagementAssessmentPreparation(view);
                }
            });
        }
    }

    private void resetDetailView() {
        textView_projectName.setText("Please select or add a project");
        textView_aboutDetail.setText("Project details");
        textView_criteriaDetail.setText("Criteria details");
        textView_asseccorDetail.setText("Click the subtitle to manage markers");
        student_detail__inpreparation.setText("Click the subtitle to manage students");
        button_about.setEnabled(true);
        button_criteria.setEnabled(true);
        button_student.setEnabled(true);
        button_assessor.setEnabled(true);
        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Activity_Assessment_Preparation.this,
                        "Please select or add a project.", Toast.LENGTH_SHORT).show();
            }
        });
        button_criteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Activity_Assessment_Preparation.this,
                        "Please select or add a project.", Toast.LENGTH_SHORT).show();
            }
        });
        button_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Activity_Assessment_Preparation.this,
                        "Please select or add a project.", Toast.LENGTH_SHORT).show();
            }
        });
        button_assessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Activity_Assessment_Preparation.this,
                        "Please select or add a project.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //plus button click function
    public void addProjectAssessmentPreparation(View view) {
        indexToSend = -999;
        Intent intent = new Intent(this, Activity_About_New.class);
        intent.putExtra("index", "-999");
        startActivity(intent);
    }

    public void aboutAssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_About.class);
        intent.putExtra("index", String.valueOf(indexToSend));
        startActivityForResult(intent, 2);
    }

    public void studentManagementAssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_Student_Management.class);
        intent.putExtra("index", String.valueOf(indexToSend));
        intent.putExtra("from", FROMPREVIOUSPROJECT);
        startActivity(intent);
    }

    public void criteriaManagementAssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_Criteria.class);
        intent.putExtra("index", String.valueOf(indexToSend));
        intent.putExtra("from", FROMPREVIOUSPROJECT);
        startActivityForResult(intent, 3);
    }

    public void markerManagementAssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_Marker_Management.class);
        intent.putExtra("index", String.valueOf(indexToSend));
        intent.putExtra("from", FROMPREVIOUSPROJECT);
        startActivityForResult(intent, 1);
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
            TextView textView_listItem = convertView.findViewById(R.id.textView_defaultView);
            textView_listItem.setText(mProjectList.get(position).getName());
            return convertView;
        }
    }


    public class MyAdapterForListView extends BaseAdapter {

        private ArrayList<Project> mProjectList;
        private Context mContext;

        public MyAdapterForListView(ArrayList<Project> projectList, Context context) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_projectlist_withdelete, parent, false);
            TextView textView_listItem = convertView.findViewById(R.id.textView_inlistView);
            textView_listItem.setText(mProjectList.get(position).getName());
            Button button = convertView.findViewById(R.id.Bt_delete_inlist);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myAdapter.notifyDataSetChanged();
                    allFunctions.deleteProject(position, AllFunctions.getObject().getProjectList().get(position).getId());
                }
            });
            return convertView;
        }
    }

    public void syncProjectList(View view) {
        allFunctions.syncProjectList();
    }

    public void updateProjectList() {
        projectList = allFunctions.getProjectList();

        MyAdapterDefaultlistView mSyncAdapter = new MyAdapterDefaultlistView
                (Activity_Assessment_Preparation.this, projectList);
        listView.setAdapter(mSyncAdapter);
        listView.setOnItemClickListener(this);
        resetDetailView();
    }

    public void bindHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108: //sync success
                        Toast.makeText(Activity_Assessment_Preparation.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        updateProjectList();
                        break;
                    case 109: // sync fail
                        Toast.makeText(Activity_Assessment_Preparation.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                bindHandler();
                showOtherInfo(indexToSend);
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                bindHandler();
                showOtherInfo(indexToSend);
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                bindHandler();
                showOtherInfo(indexToSend);
            }
        }
    }
}
