/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.feedback.Activity_Login;
import com.example.feedback.R;

import java.util.ArrayList;
import java.util.List;

import adapter.OneTwoAdapter;
import adapter.ThreeAdapter;
import bean.OneBean;
import bean.ThreeBean;
import bean.TwoBean;
import main.AllFunctions;
import newdbclass.Criterion;
import newdbclass.Project;

public class Activity_Assessment_Comment extends AppCompatActivity {

    private Criterion criteria;
    private Toolbar mToolbar;
    private int indexOfProject;
    private int indexOfCriteria;
    private int indexOfComment;
    private ThreeAdapter threeListAdapter;
    private List<ThreeBean> threeBeans;
    static int subsectionIndex;
    static int shortTextIndex;
    static int longTextIndex;

    ArrayList<ArrayList<Integer>> savedIndexList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_comment);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfCriteria = Integer.parseInt(intent.getStringExtra("indexOfCriteria"));
        indexOfComment = Integer.parseInt(intent.getStringExtra("indexOfComment"));
        Project project = AllFunctions.getObject().getProjectList().get(indexOfProject);

        if (indexOfCriteria == -999) {

        } else {
            criteria = project.getCriterionList().get(indexOfCriteria);
            savedIndexList = Activity_Assessment.getMatrixMarkedCriteria(indexOfCriteria);
        }

        if (savedIndexList.size() == 0) {
            System.out.println("zero");
        } else {
            for (int i = 0; i < savedIndexList.size(); i++) {
                for (int j = 0; j < savedIndexList.get(i).size(); j++) {
                    System.out.print(savedIndexList.get(i).get(j) + " ");
                }
                System.out.println();
            }
        }


        ListView listView = (ListView) findViewById(R.id.lv_main);
        threeListAdapter = new ThreeAdapter(this, onThreeItemClickListener);
        listView.setAdapter(threeListAdapter);


        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.elv_main);
        OneTwoAdapter expandCheckAdapter = new OneTwoAdapter(this, onTwoItemClickListener);
        expandableListView.setAdapter(expandCheckAdapter);

        List<OneBean> oneBeans = new ArrayList<>();
        for (int i = 0; i < criteria.getFieldList().size(); i++) {
            List<TwoBean> twoBeans = new ArrayList<>();
            for (int j = 0; j < criteria.getFieldList().get(i).getCommentList().size(); j++) {
                twoBeans.add(new TwoBean(false, criteria.getFieldList().get(i).getCommentList().get(j).getText()));

                if (savedIndexList.size() != 0) {
                    for (int m = 0; m < savedIndexList.size(); m++) {
                        for (int n = 0; n < 3; n++) {

                            if (i == savedIndexList.get(m).get(0) && j == savedIndexList.get(m).get(1)) {
                                twoBeans.get(j).setChecked(true);
                            }
                        }
                    }
                }
            }

            oneBeans.add(new OneBean(twoBeans, criteria.getFieldList().get(i).getName()));
        }

        expandCheckAdapter.notifyDataSetChanged(oneBeans);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_assessment_comment);
        mToolbar.setTitle("Project -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Activity_Assessment_Comment.this, Activity_Assessment.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Assessment_Comment.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Assessment_Comment.this,
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

    private OneTwoAdapter.OnTwoItemClickListener onTwoItemClickListener = new OneTwoAdapter.OnTwoItemClickListener() {
        @Override
        public void onClick(int groupId, int childId) {
            if (threeBeans == null)
                threeBeans = new ArrayList<>();
            threeBeans.clear();

            for (int i = 0; i < criteria.getFieldList().get(groupId).getCommentList().get(childId).getExpandedCommentList().size(); i++) {
                threeBeans.add(new ThreeBean(false, criteria.getFieldList().get(groupId).getCommentList().get(childId).getExpandedCommentList().get(i).getText(), i));

                if (savedIndexList.size() != 0) {
                    for (int m = 0; m < savedIndexList.size(); m++) {
                        for (int n = 0; n < 3; n++) {
                            if (groupId == savedIndexList.get(m).get(0) && childId == savedIndexList.get(m).get(1) && i == savedIndexList.get(m).get(2)) {
                                threeBeans.get(i).setChecked(true);
                            }
                        }
                    }
                }
            }
            threeListAdapter.notifyDataSetChanged(threeBeans, groupId, childId);
        }
    };

    private ThreeAdapter.OnThreeItemClickListener onThreeItemClickListener = new ThreeAdapter.OnThreeItemClickListener() {
        @Override
        public void onClick(int childId) {

            List<ThreeBean> threeSelect = threeListAdapter.getThreeSelect();
            if (threeSelect.size() > 0) {
                subsectionIndex = threeListAdapter.getOneItemSelect();
                shortTextIndex = threeListAdapter.getTwoItemSelect();
                longTextIndex = Integer.valueOf(TextUtils.join(", ", threeSelect));

                if (indexOfCriteria == -999) {

                } else {
                    Activity_Assessment.saveCommentToMatrixCriteria(indexOfCriteria, threeListAdapter.getOneItemSelect(), threeListAdapter.getTwoItemSelect(), Integer.valueOf(TextUtils.join(", ", threeSelect)));
                }
                refreshComment();
            }
        }

    };

    public void commentDone(View view) {
        if (indexOfCriteria == -999) {

        } else {
            if (Activity_Assessment.markedCriteriaSelectedAll(indexOfCriteria)) {
                finish();
                Intent intent = new Intent(this, Activity_Assessment.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You need to select a comment for each subsection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refreshComment() {
        if (indexOfCriteria == -999) {

        } else {
            savedIndexList = Activity_Assessment.getMatrixMarkedCriteria(indexOfCriteria);
        }
    }

    public void onBackPressed() {
        finish();
        Intent intent = new Intent(Activity_Assessment_Comment.this, Activity_Assessment.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
