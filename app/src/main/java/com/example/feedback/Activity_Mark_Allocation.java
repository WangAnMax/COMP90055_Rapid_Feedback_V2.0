/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Comment;
import newdbclass.Criterion;
import newdbclass.ExpandedComment;
import newdbclass.Field;
import newdbclass.Project;


public class Activity_Mark_Allocation extends AppCompatActivity {
    private int indexOfProject;
    private GridView gridView;
    private Handler handler;
    private Project project;
    private int projectId;
    private ArrayList<Criterion> markingCriteriaList;
    ArrayList<Criterion> allCriteriaList;
    private int markedCriteriaNum;
    private Toolbar mToolbar;
    private Button saveButton;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_allocation);
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("index"));
        from = intent.getStringExtra("from");
        init();
    }

    public void init() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_Mark_Allocation.this,
                                "Sync success", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 109:
                        Toast.makeText(Activity_Mark_Allocation.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    case 114:
                        Toast.makeText(Activity_Mark_Allocation.this,
                                "Successfully update the criteria of the project.", Toast.LENGTH_SHORT).show();
                        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else if (from.equals(Activity_Assessment_Preparation.FROMNEWPROJECT)) {
                            Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Marker_Management.class);
                            intent.putExtra("index", String.valueOf(indexOfProject));
                            intent.putExtra("from", Activity_Assessment_Preparation.FROMNEWPROJECT);
                            startActivity(intent);
                        }
                        break;
                    case 115:
                        Toast.makeText(Activity_Mark_Allocation.this,
                                "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        saveButton = findViewById(R.id.button_next_markAllocation);
        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
            saveButton.setText(R.string.save_button);
        }
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        projectId = project.getId();
        markingCriteriaList = project.getCriterionList();
        markedCriteriaNum = project.getCriterionList().size();
        allCriteriaList = new ArrayList<>();
        allCriteriaList.addAll(markingCriteriaList);
        initToolbar();
        MyAdapter myAdapter = new MyAdapter(allCriteriaList, this);
        gridView = findViewById(R.id.gridView_CriteriaList_markAllocation);
        gridView.setAdapter(myAdapter);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_mark_allocation);
        mToolbar.setTitle(project.getName() + " -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Criteria.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Mark_Allocation.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Mark_Allocation.this,
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
        Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Criteria.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public boolean isValidIncrementAndMaxMark() {
        for (int i = 0; i < markingCriteriaList.size(); i++) {
            if (markingCriteriaList.get(i).getMarkIncrement() == 0) {
                markingCriteriaList.get(i).setMarkIncrement(0.25);
            }

            if (markingCriteriaList.get(i).getMaximumMark() == 0) {
                Toast.makeText(Activity_Mark_Allocation.this, "Maximum mark cannot be zero.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    //button 'next'.
    public void nextMarkAllocation(View view) {
        if (isValidIncrementAndMaxMark() == true) {
            if (isValidCriteriaList()) {
                AllFunctions.getObject().updateProjectCriteria(project.getCriterionList(), projectId);
            } else {
                Toast.makeText(Activity_Mark_Allocation.this, "Some crieria is not complete. Please check and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValidCriteriaList() {
        ArrayList<Criterion> criteriaList = AllFunctions.getObject().getProjectList().get(indexOfProject).getCriterionList();
        for (int i = 0; i < criteriaList.size(); i++) {
            ArrayList<Field> fieldList = criteriaList.get(i).getFieldList();
            if (fieldList.size() == 0) {
                return false;
            } else {
                for (int j = 0; j < fieldList.size(); j++) {
                    ArrayList<Comment> commentList = fieldList.get(j).getCommentList();
                    if (commentList.size() == 0) {
                        return false;
                    } else {
                        for (int m = 0; m < commentList.size(); m++) {
                            ArrayList<ExpandedComment> expandedCommentList = commentList.get(m).getExpandedCommentList();
                            if (expandedCommentList.size() == 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Criterion> criteriaList;

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
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position < markedCriteriaNum) {
                if (convertView == null)
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_markallocation, parent, false);

                TextView textView_criteriaName = convertView.findViewById(R.id.textView_criteriaName_gridItem);
                textView_criteriaName.setText(criteriaList.get(position).getName());
                EditText editText_maxMark = convertView.findViewById(R.id.editText_maxMark_gridItem);
                editText_maxMark.setText(String.valueOf((int) criteriaList.get(position).getMaximumMark()));
                double markIncrement = criteriaList.get(position).getMarkIncrement();
                if (markIncrement != 0) {
                    if (markIncrement == 0.25) {
                        RadioButton radioButton_quarter = convertView.findViewById(R.id.radioButton_quarter_gridItem);
                        radioButton_quarter.setChecked(true);
                    } else if (markIncrement == 0.5) {
                        RadioButton radioButton_half = convertView.findViewById(R.id.radioButton_half_gridItem);
                        radioButton_half.setChecked(true);
                    } else if (markIncrement == 1) {
                        RadioButton radioButton_full = convertView.findViewById(R.id.radioButton_full_gridItem);
                        radioButton_full.setChecked(true);
                    }
                }

                RadioGroup radioGroup = convertView.findViewById(R.id.radioGroup_markIncrement_gridItem);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup rG, int checkID) {
                        switch (checkID) {
                            case R.id.radioButton_quarter_gridItem:
                                markingCriteriaList.get(position).setMarkIncrement(0.25);
                                break;
                            case R.id.radioButton_half_gridItem:
                                markingCriteriaList.get(position).setMarkIncrement(0.5);
                                break;
                            case R.id.radioButton_full_gridItem:
                                markingCriteriaList.get(position).setMarkIncrement(1);
                                break;
                            default:
                                break;
                        }
                    }
                });

                Button button_plus = convertView.findViewById(R.id.button_plus_gridItem);
                button_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mark = Integer.parseInt(editText_maxMark.getText().toString());
                        markingCriteriaList.get(position).setMaximumMark(mark + 1);
                        editText_maxMark.setText(String.valueOf(mark + 1));
                    }
                });

                Button button_minus = convertView.findViewById(R.id.button_minus_gridItem);
                button_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mark = Integer.parseInt(editText_maxMark.getText().toString());
                        if (mark > 0) {
                            markingCriteriaList.get(position).setMaximumMark(mark - 1);
                            editText_maxMark.setText(String.valueOf(mark - 1));
                        }
                    }
                });


                Button button_commentDetail = convertView.findViewById(R.id.button_commentsDetail_gridItem);
                button_commentDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Show_Comment_Mark_Allocation.class);
                        intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                        intent.putExtra("indexOfCriteria", String.valueOf(position));
                        startActivity(intent);
                    }
                });
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_commentonly, parent, false);

                TextView textView_criteriaName = convertView.findViewById(R.id.textView_criteriaName_gridItemCommentOnly);
                textView_criteriaName.setText(criteriaList.get(position).getName());

                Button button_commentDetail = convertView.findViewById(R.id.button_showComments_gridItemCommentOnly);
                button_commentDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Show_Comment_Mark_Allocation.class);
                        intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                        intent.putExtra("indexOfCriteria", String.valueOf(position));
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }
    }
}
