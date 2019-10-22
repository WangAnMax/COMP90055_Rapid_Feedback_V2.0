package com.example.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
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

import java.util.ArrayList;

import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import main.AllFunctions;

public class Activity_Display_Mark extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfStudent;
    private int indexOfGroup;
    private ArrayList<Mark> marks;
    private Handler handler;
    private Toolbar mToolbar;
    private ProjectInfo project;
    private StudentInfo student;
    private String from;
    public static final String FROMREALTIME = "realtime";
    public static final String FROMREVIEW = "review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("EEEE", "new reaper mark activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mark);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 301: //means getMark success
                        Log.d("EEEE", "get mark success");
                        init();
                        break;
                    default:
                        break;
                }
            }
        };

        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentInfo().get(indexOfStudent);
        AllFunctions.getObject().setHandler(handler);
        AllFunctions.getObject().getMarks(project, indexOfGroup, student.getNumber());
    }

    public void onNewIntent(Intent intent) {
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        Log.d("EEEE", "new display mark activity");
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 301: //means getMark success
                        Log.d("EEEE", "get mark success new intent");
                        init();
                        break;
                    default:
                        break;
                }
            }
        };

        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentInfo().get(indexOfStudent);
        AllFunctions.getObject().setHandler(handler);
        AllFunctions.getObject().getMarks(project, indexOfGroup, student.getNumber());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_reaper_mark);
        mToolbar.setTitle("Mark -- Welcome, " + AllFunctions.getObject().getUsername());
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
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
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
        marks = AllFunctions.getObject().getMarkListForMarkPage();
        MyAdapterForGridView myAdapterForGridView = new MyAdapterForGridView(marks, this);
        GridView gridViewMark = findViewById(R.id.listView_markItem_markPage);
        gridViewMark.setAdapter(myAdapterForGridView);
    }

    public void refreshMarkPage(View v) {
        AllFunctions.getObject().setHandler(handler);
        AllFunctions.getObject().getMarks(project, indexOfGroup, student.getNumber());
    }


    public class MyAdapterForGridView extends BaseAdapter {

        private Context mContext;
        private ArrayList<Mark> markList;

        public MyAdapterForGridView(ArrayList<Mark> markList, Context context) {
            this.markList = markList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return markList.size();
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
            if (markList.get(position).getTotalMark() != -999) {
                textView_totalMark.setText(markList.get(position).getTotalMark() + "%");
            } else {
                textView_totalMark.setText("Marking...");
            }

            TextView textView_assessorName = convertView.findViewById(R.id.textView_assessorName_gridItemMark);
            textView_assessorName.setText(markList.get(position).getLecturerName());

            ListView listView_gridCriteria = convertView.findViewById(R.id.listView_criteriaMark_gridItemMark);
            MyAdapterForGridItem myAdapterForGridItem = new MyAdapterForGridItem(markList.get(position), convertView.getContext());
            listView_gridCriteria.setAdapter(myAdapterForGridItem);
            setListViewHeightBasedOnChildren(listView_gridCriteria);

            Button button_viewReport = convertView.findViewById(R.id.button_viewReport_gridItemMark);
            button_viewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (from.equals(Activity_Assessment.FROMREALTIME) || from.equals(Activity_Send_Report_Individual.FROMREALTIMESEND)) {
                        if (indexOfGroup == -999) {
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
                        if (indexOfGroup == -999) {
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
            if (markList.get(position).getTotalMark() == -999) {
                button_viewReport.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }


    public class MyAdapterForGridItem extends BaseAdapter {
        private Context mContext;
        private Mark markItem;

        public MyAdapterForGridItem(Mark markItem, Context context) {
            this.markItem = markItem;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return markItem.getCriteriaList().size();
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
            textView_markWithTotalMark.setText(markItem.getMarkList().get(position) + "/" +
                    Double.valueOf(markItem.getCriteriaList().get(position).getMaximunMark()));
            TextView textView_criteriaName = convertView.findViewById(R.id.textView_criteriaName_listItemCriteriaMark);
            textView_criteriaName.setText(markItem.getCriteriaList().get(position).getName());
            convertView.setEnabled(false);

            return convertView;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
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
