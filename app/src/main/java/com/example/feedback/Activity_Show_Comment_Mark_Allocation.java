package com.example.feedback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import dbclass.Criteria;
import dbclass.ProjectInfo;
import dbclass.ShortText;
import dbclass.SubSection;
import main.AllFunctions;

public class Activity_Show_Comment_Mark_Allocation extends AppCompatActivity {
    private Criteria criteria;
    private Toolbar mToolbar;
    private ListView listView_longText;
    private MyAdapterForLongText myAdapterForLongText;
    private int indexOfSubsection = -999;
    private int indexOfShortText = -999;
    private int indexOfProject;
    private int indexOfCriteria;
    private Spinner spinner_shortText;
    private ExpandableListView expandableListView_comments_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment_markallocation);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfCriteria = Integer.parseInt(intent.getStringExtra("indexOfCriteria"));
        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);

        if (indexOfCriteria >= project.getCriteria().size()) {
            Log.d("EEEE", "large index");
            criteria = project.getCommentList().get(indexOfCriteria - project.getCriteria().size());
        } else {
            Log.d("EEEE", "small index");
            criteria = project.getCriteria().get(indexOfCriteria);
        }
        init();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_show_comment_markallocation);
        mToolbar.setTitle("Project -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
//        mToolbar.inflateMenu(R.menu.menu_toolbar);
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Show_Comment_Mark_Allocation.this,
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

    private void init() {
        expandableListView_comments_left = findViewById(R.id.expandableListView_showComment);
        MyAdapterForCommentLeft myAdapterForCommentLeft = new MyAdapterForCommentLeft(this, criteria.getSubsectionList());
        expandableListView_comments_left.setAdapter(myAdapterForCommentLeft);
        expandableListView_comments_left.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expandableListView_comments_left.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Toast.makeText(getApplicationContext(), "Double Click", Toast.LENGTH_SHORT).show();
                Log.d("EEEE", "comment child to gray");
                for (int j = 0; j < expandableListView.getChildCount(); j++)
                    expandableListView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(Color.GRAY);
                return false;
            }
        });
        expandableListView_comments_left.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < criteria.getSubsectionList().size(); i++) {
                    if (i != groupPosition && expandableListView_comments_left.isGroupExpanded(groupPosition)) {
                        Log.d("EEEE", "check");
                        expandableListView_comments_left.collapseGroup(i);
                    }
                }
            }
        });
        listView_longText = findViewById(R.id.listView_longText_showComment);
    }

    public void edit_longText_showComment(View view) {
        if (listView_longText.getCheckedItemPosition() == -1)
            Toast.makeText(this, "Please choose a comment.", Toast.LENGTH_SHORT).show();
        else {
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Show_Comment_Mark_Allocation.this);//获得layoutInflater对象
            View view2 = layoutInflater.from(Activity_Show_Comment_Mark_Allocation.this).inflate(R.layout.dialog_edit_comment, null);//获得view对象

            final EditText editText_editLongText = view2.findViewById(R.id.editText_editLongText_editComment);//获取控件
            editText_editLongText.setText(criteria.getSubsectionList().get(indexOfSubsection).getShortTextList().
                    get(indexOfShortText).getLongtext().get(listView_longText.getCheckedItemPosition()));

            Dialog dialog = new AlertDialog.Builder(Activity_Show_Comment_Mark_Allocation.this).setTitle("Edit Comment").
                    setView(view2).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String newLongText = editText_editLongText.getText().toString();
                    criteria.getSubsectionList().get(indexOfSubsection).getShortTextList().get(indexOfShortText).getLongtext().
                            set(listView_longText.getCheckedItemPosition(), newLongText);
                    myAdapterForLongText.notifyDataSetChanged();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            }).create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void delete_showComment(View view) {
        if (listView_longText.getCheckedItemPosition() == -1)
            Toast.makeText(this, "Please choose a comment.", Toast.LENGTH_SHORT).show();
        else {
            ArrayList<String> expandedCommentList = criteria.getSubsectionList().get(indexOfSubsection).getShortTextList().get(indexOfShortText).getLongtext();
            if (expandedCommentList.size() == 0) {
                Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "No comments to delete", Toast.LENGTH_SHORT).show();
            } else {
                expandedCommentList.remove(listView_longText.getCheckedItemPosition());
                myAdapterForLongText.notifyDataSetChanged();
            }
        }
    }

    public void add_showComment(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Show_Comment_Mark_Allocation.this);//获得layoutInflater对象
        final View view2 = layoutInflater.from(Activity_Show_Comment_Mark_Allocation.this).
                inflate(R.layout.dialog_add_comment, null);//获得view对象

        final Spinner spinner_subsection = view2.findViewById(R.id.spinner_subsection_addComment);
        MyAdapterForSubsectionSpinner myAdapterForSubsectionSpinner = new MyAdapterForSubsectionSpinner(view2.getContext(), criteria.getSubsectionList());
        spinner_subsection.setAdapter(myAdapterForSubsectionSpinner);
        spinner_subsection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Button helpButton = view2.findViewById(R.id.help_add_comment);
                helpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Show_Comment_Mark_Allocation.this);
                        final View view2 = layoutInflater.from(Activity_Show_Comment_Mark_Allocation.this).inflate(R.layout.dialog_help, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Show_Comment_Mark_Allocation.this);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setView(view2);
                        dialog.show();
                        ImageView imageView = view2.findViewById(R.id.imageView_dialog_help);
                        imageView.setBackgroundResource(R.drawable.criteria);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = 1700;
                        params.height = 1550;
                        dialog.getWindow().setAttributes(params);
                    }
                });
                if (position >= criteria.getSubsectionList().size()) {
                    spinner_shortText = view2.findViewById(R.id.spinner_shortText_addComment);
                    //MyAdapterForShortTextSpinner myAdapterForShortTextSpinner = new MyAdapterForShortTextSpinner(view2.getContext(), null);
                    spinner_shortText.setAdapter(null);
                    RelativeLayout relativeLayoutNewCommentSpinner = view2.findViewById(R.id.relativelayout_new_comment_spinner);
                    relativeLayoutNewCommentSpinner.setVisibility(View.INVISIBLE);
                    RelativeLayout relativeLayoutNewSubsection = view2.findViewById(R.id.relativellayout_new_subsection);
                    relativeLayoutNewSubsection.setVisibility(View.VISIBLE);
                    RelativeLayout relativeLayoutNewComment = view2.findViewById(R.id.relativellayout_new_comment);
                    relativeLayoutNewComment.setVisibility(View.VISIBLE);
                } else {
                    spinner_shortText = view2.findViewById(R.id.spinner_shortText_addComment);
                    MyAdapterForShortTextSpinner myAdapterForShortTextSpinner = new MyAdapterForShortTextSpinner(view2.getContext(),
                            criteria.getSubsectionList().get(position).getShortTextList());
                    spinner_shortText.setAdapter(myAdapterForShortTextSpinner);
                    RelativeLayout relativeLayoutNewCommentSpinner = view2.findViewById(R.id.relativelayout_new_comment_spinner);
                    relativeLayoutNewCommentSpinner.setVisibility(View.VISIBLE);
                    RelativeLayout relativeLayoutNewSubsection = view2.findViewById(R.id.relativellayout_new_subsection);
                    relativeLayoutNewSubsection.setVisibility(View.INVISIBLE);

                    int commentListSize = criteria.getSubsectionList().get(position).getShortTextList().size();
                    spinner_shortText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position >= commentListSize) {
                                RelativeLayout relativeLayoutNewComment = view2.findViewById(R.id.relativellayout_new_comment);
                                relativeLayoutNewComment.setVisibility(View.VISIBLE);
                            } else {
                                RelativeLayout relativeLayoutNewComment = view2.findViewById(R.id.relativellayout_new_comment);
                                relativeLayoutNewComment.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            return;
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Show_Comment_Mark_Allocation.this);
        builder.setTitle("Add comment");
        builder.setView(view2);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int whichSubsection = spinner_subsection.getSelectedItemPosition();
                if (whichSubsection < criteria.getSubsectionList().size()) {
                    int whichShortText = spinner_shortText.getSelectedItemPosition();
                    if (whichShortText < criteria.getSubsectionList().get(whichSubsection).getShortTextList().size()) {
                        EditText editText_longText = view2.findViewById(R.id.editText_longText_addComment);
                        String longText = editText_longText.getText().toString().trim();
                        if (longText.equals("")) {
                            Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The Expanded comment cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            criteria.getSubsectionList().get(whichSubsection).getShortTextList().get(whichShortText).getLongtext().add(longText);
                            init();
                            dialog.dismiss();
                        }
                    } else {
                        EditText editText_shortText = view2.findViewById(R.id.editText_shortText_addComment);
                        String shortText = editText_shortText.getText().toString().trim();
                        if (shortText.equals("")) {
                            Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The comment cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            EditText editText_shortTextGrade = view2.findViewById(R.id.editText_shortTextgrade_addComment);
                            String shortTextGrade = editText_shortTextGrade.getText().toString().trim();
                            if (shortTextGrade.equals("")) {
                                Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The comment grade cannot be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                EditText editText_longText = view2.findViewById(R.id.editText_longText_addComment);
                                String longText = editText_longText.getText().toString().trim();
                                if (longText.equals("")) {
                                    Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The expanded comment cannot be empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    ShortText shortText_ls = new ShortText();
                                    shortText_ls.setName(shortText);
                                    shortText_ls.setGrade(Integer.parseInt(shortTextGrade));
                                    shortText_ls.getLongtext().add(longText);
                                    criteria.getSubsectionList().get(whichSubsection).getShortTextList().add(shortText_ls);
                                    init();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
                } else {
                    EditText editText_otherSubsection = view2.findViewById(R.id.editText_otherSubsection_addComment);
                    String subsectionName = editText_otherSubsection.getText().toString().trim();
                    if (subsectionName.equals("")) {
                        Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The subsection cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        EditText editText_shortText = view2.findViewById(R.id.editText_shortText_addComment);
                        String shortText = editText_shortText.getText().toString().trim();
                        if (shortText.equals("")) {
                            Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The comment cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            EditText editText_shortTextGrade = view2.findViewById(R.id.editText_shortTextgrade_addComment);
                            String shortTextGrade = editText_shortTextGrade.getText().toString().trim();
                            if (shortTextGrade.equals("")) {
                                Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The comment grade cannot be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                EditText editText_longText = view2.findViewById(R.id.editText_longText_addComment);
                                String longText = editText_longText.getText().toString().trim();
                                if (longText.equals("")) {
                                    Toast.makeText(Activity_Show_Comment_Mark_Allocation.this, "The expanded comment cannot be empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    ShortText shortText_ls = new ShortText();
                                    shortText_ls.setName(shortText);
                                    shortText_ls.setGrade(Integer.parseInt(shortTextGrade));
                                    shortText_ls.getLongtext().add(longText);
                                    SubSection subSection_ls = new SubSection();
                                    subSection_ls.setName(subsectionName);
                                    subSection_ls.getShortTextList().add(shortText_ls);
                                    criteria.getSubsectionList().add(subSection_ls);
                                    init();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    class MyAdapterForCommentLeft extends BaseExpandableListAdapter {
        private Context mContext;
        private ArrayList<SubSection> subSections;

        public MyAdapterForCommentLeft(Context context, ArrayList<SubSection> subSections) {
            this.mContext = context;
            this.subSections = subSections;
        }

        @Override
        public int getGroupCount() {
            return subSections.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return subSections.get(groupPosition).getShortTextList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_subsection_showcomment, null);
            TextView textView_subsection = convertView.findViewById(R.id.textView_subsection_showComment);
            textView_subsection.setText(criteria.getSubsectionList().get(groupPosition).getName());
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_shorttext_showcomments, null);
            TextView textView_shortText = convertView.findViewById(R.id.textView_shortText_showComments);
            textView_shortText.setText(criteria.getSubsectionList().get(groupPosition).getShortTextList().get(childPosition).getName());
            final View convertView2 = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAdapterForLongText = new MyAdapterForLongText(Activity_Show_Comment_Mark_Allocation.this,
                            subSections.get(groupPosition).getShortTextList().get(childPosition).getLongtext());
                    listView_longText.setAdapter(myAdapterForLongText);
                    indexOfSubsection = groupPosition;
                    indexOfShortText = childPosition;
                    Log.d("EEEE", "child position； " + childPosition);
                    Log.d("EEEE", "child count: " + parent.getChildCount());
                    convertView2.setSelected(true);
                }
            });

            if (convertView.isSelected()) {
                Log.d("EEEE", "click shorttext");
                convertView.setBackgroundColor(Color.GRAY);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class MyAdapterForLongText extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> longTexts;

        public MyAdapterForLongText(Context context, ArrayList<String> longTexts) {
            this.mContext = context;
            this.longTexts = longTexts;
        }

        @Override
        public int getCount() {
            return longTexts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_longtext_showcomments, null);
            TextView textView_longText = convertView.findViewById(R.id.textView_longText_showComments);
            textView_longText.setText(longTexts.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listView_longText.isItemChecked(position))
                        listView_longText.setItemChecked(position, false);
                    else
                        listView_longText.setItemChecked(position, true);
                }
            });
            if (listView_longText.isItemChecked(position)) {
                Log.d("EEEE", "longtext is clicked");
                convertView.setBackgroundColor(Color.GRAY);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
    }

    class MyAdapterForSubsectionSpinner extends BaseAdapter {
        private Context mContext;
        private ArrayList<SubSection> subSections;

        public MyAdapterForSubsectionSpinner(Context context, ArrayList<SubSection> subSections) {
            this.mContext = context;
            this.subSections = subSections;
        }

        @Override
        public int getCount() {
            return subSections.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_spinner, null);
            if (position < subSections.size()) {
                TextView textView = convertView.findViewById(R.id.textView_content_spinner);
                textView.setText(subSections.get(position).getName());
            } else {
                TextView textView = convertView.findViewById(R.id.textView_content_spinner);
                textView.setText("Add new subsection");
            }
            return convertView;
        }
    }


    class MyAdapterForShortTextSpinner extends BaseAdapter {
        private Context mContext;
        private ArrayList<ShortText> shortTexts;

        public MyAdapterForShortTextSpinner(Context context, ArrayList<ShortText> shortTexts) {
            this.mContext = context;
            this.shortTexts = shortTexts;
        }

        @Override
        public int getCount() {
            return shortTexts.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_spinner, null);
            if (position < shortTexts.size()) {
                TextView textView = convertView.findViewById(R.id.textView_content_spinner);
                textView.setText(shortTexts.get(position).getName());
            } else {
                TextView textView = convertView.findViewById(R.id.textView_content_spinner);
                textView.setText("Add new comment");
            }
            return convertView;
        }
    }
}