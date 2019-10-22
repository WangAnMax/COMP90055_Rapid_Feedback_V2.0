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

    // 记录文件名
    private static String fileName;
    // 记录目录路径
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
        Log.d("EEEE", fileDir);
    }

//    // 1、检查是否有读写sdcard的权限
//    private void checkWriteAndReadPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
//                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permissions, 1000);
//            }
//
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        for (int permission : grantResults) {
//            if (permission == PackageManager.PERMISSION_DENIED) {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
//                break;
//            }
//        }
//    }

    // 2、创建文件目录
    private static boolean createDir(String dir) {
        File fileDir = new File(dir);
        if (fileDir.exists() && fileDir.isDirectory()) {
            return true;
        } else {
            return fileDir.mkdirs();
        }

    }

    // 3、创建文件
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

    // 4、向文件写入数据
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

    // 5、检查是否写入成功
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

