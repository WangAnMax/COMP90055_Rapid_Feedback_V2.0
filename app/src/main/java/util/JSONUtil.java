/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JSONUtil {

    // record file name
    private static String fileName;
    // record file path
    private static String fileDir;


    public static void write(String receive) {
        fileDir = Environment.getExternalStorageDirectory() + "/CC_FILE";
        if (createDir(fileDir)) {
            fileName = "receive.json";
            File file = createFile(fileName);
            if (file != null && file.exists()) {
                write2File(file, receive);
//                showFileContent(fileDir, fileName);
            }
        }
    }

    private static boolean createDir(String dir) {
        File fileDir = new File(dir);
        if (fileDir.exists() && fileDir.isDirectory()) {
            return true;
        } else {
            return fileDir.mkdirs();
        }

    }

    private static File createFile(String fileName) {
        File file = new File(fileDir, fileName);
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            try {
                if (file.createNewFile()) {
                    return file;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static void write2File(File file, String data) {

        OutputStream ou = null;
        try {
            ou = new FileOutputStream(file);
            byte[] buffer = data.getBytes();
            ou.write(buffer);
            ou.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void showFileContent(String dir, String fileName) {
        File file = new File(dir, fileName);
        if (file.exists() && file.isFile()) {
            InputStream in = null;
            try {
                StringBuilder stringBuilder = new StringBuilder();
                in = new FileInputStream(file);
                byte[] buffer = new byte[4 * 1024];
                while ((in.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

