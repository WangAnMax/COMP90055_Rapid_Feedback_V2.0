/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Criterion;
import newdbclass.Project;
import util.DefaultCriteriaList;
import util.FileUtils;

public class Activity_Criteria extends AppCompatActivity {

    private Project project;
    private int indexOfProject;
    private ArrayList<Criterion> defaultCriteriaList;
    private ListView listView_criteriaDefault;
    private ListView listView_marketCriteria;
    private Handler handler;
    private String path;
    private Toolbar mToolbar;
    private AlertDialog dialog;
    private int projectId;
    private MyAdapter_criteriaListDefault myAdapter1;
    private MyAdapter_criteriaListDefault myAdapter2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private String from;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria);
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("index"));
        from = intent.getStringExtra("from");
        init();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_criteria);
        mToolbar.setTitle(project.getName() + " -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
                    discardWarning();
                } else if (from.equals(Activity_Assessment_Preparation.FROMNEWPROJECT)) {
                    AllFunctions.getObject().deleteProject(indexOfProject, projectId);
                }
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Criteria.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Criteria.this,
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

    protected void onNewIntent(Intent intent) {
        Intent intent2 = getIntent();
        indexOfProject = Integer.parseInt(intent2.getStringExtra("index"));
        init();
    }

    private void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_Criteria.this,
                                "Sync success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        break;
                    case 109:
                        Toast.makeText(Activity_Criteria.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    case 112:
                        Toast.makeText(Activity_Criteria.this,
                                "Successfully delete the project.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 113:
                        Toast.makeText(Activity_Criteria.this,
                                "Fail to delete the project. Please try again.", Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        projectId = project.getId();
        defaultCriteriaList = DefaultCriteriaList.getDefaultCriteriaList();
        defaultCriteriaList.removeAll(project.getCriterionList());
        listView_criteriaDefault = findViewById(R.id.listView_CriteriaList_inCriteriaList);
        listView_marketCriteria = findViewById(R.id.listView_markingCriteria_inCriteriaList);

        myAdapter1 = new MyAdapter_criteriaListDefault(defaultCriteriaList, this);
        myAdapter2 = new MyAdapter_criteriaListDefault(project.getCriterionList(), this);

        listView_criteriaDefault.setAdapter(myAdapter1);
        listView_marketCriteria.setAdapter(myAdapter2);

        listView_criteriaDefault.setOnDragListener(dragListenerForDefaultListview);
        listView_marketCriteria.setOnDragListener(dragListenerForMarkingCriteriaList);

        initToolbar();
    }

    //button next.
    public void nextCriteria(View view) {
        if (project.getCriterionList().size() == 0) {
            Toast.makeText(Activity_Criteria.this, "Marking criteria cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, Activity_Mark_Allocation.class);
            intent.putExtra("index", String.valueOf(indexOfProject));
            intent.putExtra("from", from);
            startActivityForResult(intent, 1);
        }
    }

    public void helpCriteriaUpload(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Criteria.this);
        final View view2 = layoutInflater.from(Activity_Criteria.this).inflate(R.layout.dialog_help, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Criteria.this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog = builder.create();
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

    public void addMarkedCriteria(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);//获得layoutInflater对象
        final View view2 = layoutInflater.from(this).inflate(R.layout.dialog_add_criteria, null);//获得view对象

        Dialog dialog = new android.app.AlertDialog.Builder(this).setView(view2).
                setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText_newCriteriaName = view2.findViewById(R.id.editText_criteriaName_dialogAddCriteria);//获取控件
                        String newCriteriaName = editText_newCriteriaName.getText().toString();

                        if (findWhichCriteriaList_itbelongs(newCriteriaName) == -999) {
                            Criterion criteria = new Criterion();
                            criteria.setName(newCriteriaName);
                            project.getCriterionList().add(criteria);
                            init();
                        } else {
                            Toast.makeText(Activity_Criteria.this, "Criteria " + newCriteriaName + " has been existed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).create();
        dialog.show();
    }

    public class MyAdapter_criteriaListDefault extends BaseAdapter {
        private Context mContext;
        private ArrayList<Criterion> criteriaList;

        public MyAdapter_criteriaListDefault(ArrayList<Criterion> criteriaListList, Context context) {
            this.criteriaList = criteriaListList;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_criterialist_default, parent, false);
            TextView textView_criteriaName = convertView.findViewById(R.id.textView_criterialistDefault_inCriteriaList);
            textView_criteriaName.setText(criteriaList.get(position).getName());

            final View dragView = convertView;
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData.Item item1 = new ClipData.Item(criteriaList.get(position).getName());
                    ClipData.Item item2 = new ClipData.Item(String.valueOf(position));

                    ClipData clipData = new ClipData("", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item1);
                    clipData.addItem(item2);

                    dragView.startDrag(clipData, new View.DragShadowBuilder(dragView), null, 0);
                    return true;
                }
            });
            return convertView;
        }
    }


    private View.OnDragListener dragListenerForDefaultListview = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            // Handles each of the expected events
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    // Gets the item containing the dragged data
                    ClipData.Item item1 = event.getClipData().getItemAt(0);
                    ClipData.Item item2 = event.getClipData().getItemAt(1);
                    String source_criteriaName = item1.getText().toString();
                    int source_criteriaIndex = Integer.parseInt(item2.getText().toString());
                    int whichList = findWhichCriteriaList_itbelongs(source_criteriaName);
                    switch (whichList) {
                        case 0:
                            break;
                        case 1:
                            Criterion criteria_Temporary = project.getCriterionList().get(source_criteriaIndex);
                            project.getCriterionList().remove(source_criteriaIndex);
                            defaultCriteriaList.add(criteria_Temporary);
                            break;
                        default:
                            break;
                    }
                    myAdapter1.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // Returns true. DragEvent.getResult() will return true.
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Turns off any color tinting
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // returns true; the value is ignored.
                    return true;
                // An unknown action type was received.
                default:
                    break;
            }
            return false;
        }
    };

    private View.OnDragListener dragListenerForMarkingCriteriaList = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            // Handles each of the expected events
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    // Gets the item containing the dragged data
                    ClipData.Item item1 = event.getClipData().getItemAt(0);
                    ClipData.Item item2 = event.getClipData().getItemAt(1);
                    String source_criteriaName = item1.getText().toString();
                    int source_criteriaIndex = Integer.parseInt(item2.getText().toString());
                    int whichList = findWhichCriteriaList_itbelongs(source_criteriaName);
                    switch (whichList) {
                        case 0:
                            Criterion criteria_Temporary = defaultCriteriaList.get(source_criteriaIndex);
                            defaultCriteriaList.remove(source_criteriaIndex);
                            project.getCriterionList().add(criteria_Temporary);
                            break;
                        case 1:
                            break;
                        default:
                            break;
                    }
                    myAdapter1.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // Returns true. DragEvent.getResult() will return true.
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Turns off any color tinting
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // returns true; the value is ignored.
                    return true;
                // An unknown action type was received.
                default:
                    break;
            }
            return false;
        }
    };

    //0 means defaultCriteriaList, 1 means marking criteriaList
    private int findWhichCriteriaList_itbelongs(String criteriaName) {
        for (Criterion c : defaultCriteriaList) {
            if (c.getName().equals(criteriaName))
                return 0;
        }
        for (Criterion c : project.getCriterionList()) {
            if (c.getName().equals(criteriaName))
                return 1;
        }
        return -999;
    }

    public void discardWarning() {
        AllFunctions.getObject().setHandler(handler); // attention!!!!!!!

        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Criteria.this);
        final View view = layoutInflater.from(Activity_Criteria.this).
                inflate(R.layout.dialog_quit_editing, null);
        TextView warning = view.findViewById(R.id.textView_dialog_query_waring_editing);
        warning.setText("Are you sure to discard all changes ?");
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Criteria.this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().syncProjectList();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private static final int FILE_SELECT_CODE = 0;

    public void uploadCriteria(View view) {

        try {
            verifyStoragePermissions(Activity_Criteria.this);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    path = FileUtils.getPath(this, uri);
                    ArrayList<Criterion> uploadCriteriaList = new ArrayList<>();
                    uploadCriteriaList = AllFunctions.getObject().readCriteriaExcel(project, path);
                    defaultCriteriaList.addAll(uploadCriteriaList);
                    myAdapter1.notifyDataSetChanged();
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
            discardWarning();
        } else if (from.equals(Activity_Assessment_Preparation.FROMNEWPROJECT)) {
            AllFunctions.getObject().deleteProject(indexOfProject, projectId);
        }
    }
}
