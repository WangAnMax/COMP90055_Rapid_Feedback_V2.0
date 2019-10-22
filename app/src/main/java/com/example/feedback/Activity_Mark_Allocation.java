package com.example.feedback;

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

import dbclass.Criteria;
import dbclass.ProjectInfo;
import dbclass.ShortText;
import dbclass.SubSection;
import main.AllFunctions;


public class Activity_Mark_Allocation extends AppCompatActivity {
    private int indexOfProject;
    private GridView gridView;
    private Handler handler;
    private ProjectInfo project;
    private ArrayList<Criteria> markingCriteriaList;
    ArrayList<Criteria> allCriteriaList;
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
        Log.d("EEEE", "mark allocation start!");
    }

    public void init() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 401:
                        Log.d("EEEE", "Successfully update the criteria of the project.");
                        Toast.makeText(Activity_Mark_Allocation.this,
                                "Successfully update the criteria of the project.", Toast.LENGTH_SHORT).show();
                        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
                            Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Assessment_Preparation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else if (from.equals(Activity_Assessment_Preparation.FROMNEWPROJECT)) {
                            Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Marker_Management.class);
                            intent.putExtra("index", String.valueOf(indexOfProject));
                            intent.putExtra("from", Activity_Assessment_Preparation.FROMNEWPROJECT);
                            startActivity(intent);
                        }
                        break;
                    case 402:
                        Log.d("EEEE", "Fail to update the criteria of the project.");
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
        markingCriteriaList = project.getCriteria();
        markedCriteriaNum = project.getCriteria().size();
        allCriteriaList = new ArrayList<>();
        allCriteriaList.addAll(markingCriteriaList);
        allCriteriaList.addAll(project.getCommentList());
        initToolbar();
        MyAdapter myAdapter = new MyAdapter(allCriteriaList, this);
        gridView = findViewById(R.id.gridView_CriteriaList_markAllocation);
        gridView.setAdapter(myAdapter);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_mark_allocation);
        mToolbar.setTitle(project.getProjectName() + " -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Criteria.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
//        mToolbar.inflateMenu(R.menu.menu_toolbar);
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
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
        for(int i = 0; i < markingCriteriaList.size(); i++){
            if(markingCriteriaList.get(i).getMarkIncrement() == null) {
                markingCriteriaList.get(i).setMarkIncrement("1/4");
            }

            if(markingCriteriaList.get(i).getMaximunMark() == 0) {
                Toast.makeText(Activity_Mark_Allocation.this, "Maximum mark cannot be zero.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    //button 'next'.
    public void nextMarkAllocation(View view) {
        if(isValidIncrementAndMaxMark() == true) {
            if (isValidCriteriaList()) {
                AllFunctions.getObject().projectCriteria(project, project.getCriteria(), project.getCommentList());
            } else {
                Toast.makeText(Activity_Mark_Allocation.this, "Some crieria is not complete. Please check and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValidCriteriaList() {
        Log.d("EEEE", "check criteria list");
        ArrayList<Criteria> criteriaList = AllFunctions.getObject().getProjectList().get(indexOfProject).getCriteria();
        for (int i = 0; i < criteriaList.size(); i++) {
            ArrayList<SubSection> subsectionList = criteriaList.get(i).getSubsectionList();
            if (subsectionList.size() == 0) {
                return false;
            } else {
                for (int j = 0; j < subsectionList.size(); j++) {
                    ArrayList<ShortText> commentList = subsectionList.get(j).getShortTextList();
                    if (commentList.size() == 0) {
                        return false;
                    } else {
                        for (int m = 0; m < commentList.size(); m++) {
                            ArrayList<String> expandedCommentList = commentList.get(m).getLongtext();
                            if (expandedCommentList.size() == 0) {
                                Log.d("EEEE", "empty longtext");
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
        private ArrayList<Criteria> criteriaList;

        public MyAdapter(ArrayList<Criteria> criteriaList, Context context) {
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
                if(convertView == null)
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_markallocation, parent, false);

                TextView textView_criteriaName = convertView.findViewById(R.id.textView_criteriaName_gridItem);
                textView_criteriaName.setText(criteriaList.get(position).getName());
                EditText editText_maxMark = convertView.findViewById(R.id.editText_maxMark_gridItem);
                editText_maxMark.setText(String.valueOf(criteriaList.get(position).getMaximunMark()));
                String markIncrement = criteriaList.get(position).getMarkIncrement();
                if (markIncrement != null)
                    switch (markIncrement) {
//                case "quarter":
                        case "1/4":
                            RadioButton radioButton_quarter = convertView.findViewById(R.id.radioButton_quarter_gridItem);
                            radioButton_quarter.setChecked(true);
                            break;
//                case "half":
                        case "1/2":
                            RadioButton radioButton_half = convertView.findViewById(R.id.radioButton_half_gridItem);
                            radioButton_half.setChecked(true);
                            break;
//                case "full":
                        case "1":
                            RadioButton radioButton_full = convertView.findViewById(R.id.radioButton_full_gridItem);
                            radioButton_full.setChecked(true);
                            break;
                        default:
                            break;
                    }

                RadioGroup radioGroup = convertView.findViewById(R.id.radioGroup_markIncrement_gridItem);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup rG, int checkID) {
                        switch (checkID) {
                            case R.id.radioButton_quarter_gridItem:
//                            criteriaList.get(position).setMarkIncrement("quarter");
                                markingCriteriaList.get(position).setMarkIncrement("1/4");
                                break;
                            case R.id.radioButton_half_gridItem:
//                            criteriaList.get(position).setMarkIncrement("half");
                                markingCriteriaList.get(position).setMarkIncrement("1/2");
                                break;
                            case R.id.radioButton_full_gridItem:
//                            criteriaList.get(position).setMarkIncrement("full");
                                markingCriteriaList.get(position).setMarkIncrement("1");
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
                        Log.d("EEEE", Integer.parseInt(editText_maxMark.getText().toString()) + "");
                        markingCriteriaList.get(position).setMaximunMark(mark + 1);
                        editText_maxMark.setText(String.valueOf(mark + 1));
                    }
                });

                Button button_minus = convertView.findViewById(R.id.button_minus_gridItem);
                button_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mark = Integer.parseInt(editText_maxMark.getText().toString());
                        if(mark > 0) {
                            markingCriteriaList.get(position).setMaximunMark(mark - 1);
                            editText_maxMark.setText(String.valueOf(mark - 1));
                        }
                    }
                });


                Button button_commentDetail = convertView.findViewById(R.id.button_commentsDetail_gridItem);
                button_commentDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_Mark_Allocation.this, Activity_Show_Comment_Mark_Allocation.class);
                        intent.putExtra("indexOfProject",String.valueOf(indexOfProject));
                        intent.putExtra("indexOfCriteria",String.valueOf(position));
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
                        intent.putExtra("indexOfProject",String.valueOf(indexOfProject));
                        intent.putExtra("indexOfCriteria",String.valueOf(position));
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }
    }
}
