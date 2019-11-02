/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.feedback.RecordingItem;

public class RecordingDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;


    public static final String CREATE_BOOK = "create table Recording ("
            + "id integer primary key autoincrement,"
            + "student text,"
            + "filename text,"
            + "project text,"
            + "filelength integer,"
            + "filepath text)";

    public RecordingDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addRecording(String recordingName, String filePath, long length, String email, String project) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("filename", recordingName);
        cv.put("filepath", filePath);
        cv.put("filelength", length);
        cv.put("project", project);
        cv.put("student", email);
        db.insert("Recording", null, cv);
    }

    public void deleteRecording(String project, String email) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Recording", "project = ? and student = ?)", new String[]{project, email});
    }

    public RecordingItem getItemAt(String project, String student, RecordingItem item) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select filename, filepath, filelength from Recording where project = ? and student = ?",
                new String[]{project, student});
        while (c.moveToNext()) {
            item.setName(c.getString(c.getColumnIndex("filename")));
            item.setLength(c.getInt(c.getColumnIndex("filelength")));
            item.setFilePath(c.getString(c.getColumnIndex("filepath")));
        }

        c.close();
        return item;
    }
}
