package com.example.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import main.AllFunctions;
import newdbclass.Marker;
import newdbclass.Project;

public class Activity_Marker_Management extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Project> projectList;
    private AdapterForMarkerDeletion adapterForMarkers;
    private Handler handler;
    private String index;
    private int indexOfProject;
    private Project project;
    private CheckBox mCheckBoxDeleteMarker;
    private Button mButtonInviteMarker;
    private ListView mListViewMarkers;
    private Button mButtonNextMarkers;
    private Toolbar mToolbar;
    private AlertDialog dialog;
    private EditText mEditTextInvitee;
    private ProgressBar mProgressbarInvitation;
    private int deleteIndex;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_management);
        Log.d("EEEE", "Marker Management: onCreate has been called!");
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        Log.d("EEEE", "Marker management's index: " + index);
        from = intent.getStringExtra("from");
        init();
    }

    protected void onNewIntent(Intent intent) {
        init();
        Log.d("EEEE", "Preparation: onNewIntent has been called!");
    }

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 207:
                        Toast.makeText(Activity_Marker_Management.this,
                                "The invitation has been sent.", Toast.LENGTH_SHORT).show();
                        mProgressbarInvitation.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        mEditTextInvitee.setText("");
                        break;
                    case 208:
                        Toast.makeText(Activity_Marker_Management.this,
                                "The email has not been registered. Please check and try again.", Toast.LENGTH_SHORT).show();
                        mProgressbarInvitation.setVisibility(View.INVISIBLE);
                        mEditTextInvitee.setText("");
                        break;
                    case 309:
                        Toast.makeText(Activity_Marker_Management.this,
                                "Successfully delete the marker", Toast.LENGTH_SHORT).show();
                        break;
                    case 310:
                        Toast.makeText(Activity_Marker_Management.this,
                                "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                        init();
                        break;
                    default:
                        break;
                }
            }
        };

        initToolbar();
        indexOfProject = Integer.parseInt(index);
        AllFunctions.getObject().setHandler(handler);
        projectList = AllFunctions.getObject().getProjectList();
        project = AllFunctions.getObject().getProjectList().get(Integer.parseInt(index));
        mCheckBoxDeleteMarker = findViewById(R.id.cb_marker_delete_management);
        mButtonInviteMarker = findViewById(R.id.button_marker_add_management);
        mListViewMarkers = findViewById(R.id.listView_marker_management);
        mButtonNextMarkers = findViewById(R.id.button_next_marker_management);
        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
            mButtonNextMarkers.setVisibility(View.INVISIBLE);
        }
        mToolbar.setTitle(project.getName());
        AdapterForMarkerDisplay mAdapterDisplayMarkers = new AdapterForMarkerDisplay(
                project.getMarkerList(), Activity_Marker_Management.this);
        mListViewMarkers.setAdapter(mAdapterDisplayMarkers);
        mListViewMarkers.setOnItemClickListener(this);

        mCheckBoxDeleteMarker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    adapterForMarkers = new AdapterForMarkerDeletion(project.getMarkerList(),
                            Activity_Marker_Management.this);
                    mListViewMarkers.setAdapter(adapterForMarkers);
                    mButtonInviteMarker.setEnabled(false);
                    mButtonInviteMarker.setBackgroundResource(R.drawable.ic_add_marker_disabled);
                    mButtonNextMarkers.setEnabled(false);
                } else {
                    AdapterForMarkerDisplay mAdapterDisplayMarkers = new AdapterForMarkerDisplay(
                            project.getMarkerList(), Activity_Marker_Management.this);
                    mListViewMarkers.setAdapter(mAdapterDisplayMarkers);
                    mListViewMarkers.setOnItemClickListener(Activity_Marker_Management.this);
                    mButtonInviteMarker.setEnabled(true);
                    mButtonInviteMarker.setBackgroundResource(R.drawable.ripple_add_marker);
                    mButtonNextMarkers.setEnabled(true);
                }
            }
        });

        mButtonInviteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().setHandler(handler);
                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Marker_Management.this);//获得layoutInflater对象
                final View view2 = layoutInflater.from(Activity_Marker_Management.this).
                        inflate(R.layout.dialog_markers, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Marker_Management.this);
                builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setView(view2);
                dialog.show();

                mEditTextInvitee = view2.findViewById(R.id.editText_dialog_marker_invitation);
                mProgressbarInvitation = view2.findViewById(R.id.progressbar_marker_invitation);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEditTextInvitee.getText().toString().equals(""))
                            Toast.makeText(Activity_Marker_Management.this,
                                    "The email of an invitee cannot be empty.", Toast.LENGTH_SHORT).show();
                        else {
                            AllFunctions.getObject().inviteMarker(Integer.parseInt(mEditTextInvitee.getText().toString()));
                            mProgressbarInvitation.setVisibility(View.VISIBLE);
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEditTextInvitee.setText("");
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < parent.getChildCount(); i++)
            parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        view.setBackgroundColor(Color.parseColor("#dbdbdb"));
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_marker_management);
        mToolbar.setTitle("Project -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
//        mToolbar.inflateMenu(R.menu.menu_toolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Marker_Management.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Marker_Management.this,
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

    public class AdapterForMarkerDisplay extends BaseAdapter {
        private ArrayList<Marker> mMarkerList;
        private Context mContext;

        public AdapterForMarkerDisplay(ArrayList<Marker> assistantList, Context mContext) {
            this.mMarkerList = assistantList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mMarkerList.size();
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
            if (mMarkerList.get(position).getMiddleName().equals("")) {
                textView_listItem.setText(mMarkerList.get(position).getFirstName() + " " + mMarkerList.get(position).getLastName());
            } else {
                textView_listItem.setText(mMarkerList.get(position).getFirstName() + " " + mMarkerList.get(position).getMiddleName() + " " + mMarkerList.get(position).getLastName());
            }
            return convertView;
        }
    }

    public class AdapterForMarkerDeletion extends BaseAdapter {
        private ArrayList<Marker> mMarkerList;
        private Context mContext;

        public AdapterForMarkerDeletion(ArrayList<Marker> assistantList, Context mContext) {
            this.mMarkerList = assistantList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mMarkerList.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_markers, parent, false);
            TextView textView_listItem = convertView.findViewById(R.id.textView_markerName);
            if (mMarkerList.get(position).getMiddleName().equals("")) {
                textView_listItem.setText(mMarkerList.get(position).getFirstName() + " " + mMarkerList.get(position).getLastName());
            } else {
                textView_listItem.setText(mMarkerList.get(position).getFirstName() + " " + mMarkerList.get(position).getMiddleName() + " " + mMarkerList.get(position).getLastName());
            }
            Button button = convertView.findViewById(R.id.button_deleteMarker);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AllFunctions.getObject().deleteMarker(mMarkerList.get(position).getId());
                    deleteIndex = position;
                    mMarkerList.remove(position);
                    adapterForMarkers.notifyDataSetChanged();
                }
            });

            if (position == 0) {
                button.setVisibility(View.INVISIBLE);
                button.setEnabled(false);
            }
            return convertView;
        }
    }

//    public void nextMarkerManagement(View view) {
//        Intent intent = new Intent(Activity_Marker_Management.this, Activity_Student_Management.class);
//        intent.putExtra("index", index);
//        intent.putExtra("from", Activity_Assessment_Preparation.FROMNEWPROJECT);
//        startActivity(intent);
//    }
}
