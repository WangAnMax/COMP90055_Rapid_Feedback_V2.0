/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.AESEncrypter;

public class UserInfoOperator {

    private Context mContext;

    public UserInfoOperator(Context context) {
        mContext = context;
    }

    public void saveUserInfo(String email, String password) {
        Map<String, String> userInfo = new HashMap<>();
        SharedPreferences sp = this.mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
        try {
            if (sp != null) {
                String jsonString = sp.getString("userinfo", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    userInfo.put(key, value);
                }
                String encryptedEmail = AESEncrypter.encrypt(email);
                String encryptedPassword = AESEncrypter.encrypt(password);
                userInfo.put(encryptedEmail, encryptedPassword);
                JSONObject infoObject = new JSONObject(userInfo);
                String infoString = infoObject.toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("userinfo").commit();
                editor.putString("userinfo", infoString);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getUserInfo() {
        HashMap<String, String> userInfo = new HashMap<>();
        SharedPreferences sp = this.mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
        try {
            if (sp != null) {
                String jsonString = sp.getString("userinfo", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    String decryptedKey = AESEncrypter.decrypt(key);
                    String decryptedValue = AESEncrypter.decrypt(value);
                    userInfo.put(decryptedKey, decryptedValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public void deleteUserInfo(String email, String password) {
        Map<String, String> userInfo = new HashMap<>();
        SharedPreferences sp = this.mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
        try {
            if (sp != null) {
                String jsonString = sp.getString("userinfo", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String decryptedKey = AESEncrypter.decrypt(key);
                    if (!decryptedKey.equals(email)) {
                        String value = (String) jsonObject.get(key);
                        userInfo.put(key, value);
                    }
                }
                JSONObject infoObject = new JSONObject(userInfo);
                String infoString = infoObject.toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("userinfo").commit();
                editor.putString("userinfo", infoString);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
