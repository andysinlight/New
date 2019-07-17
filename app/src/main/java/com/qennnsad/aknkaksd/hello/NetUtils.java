package com.qennnsad.aknkaksd.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class NetUtils {
    static String Base_Url = "http://api.4kwy.top";
    final static String TAG = "hello";
    private static AlertDialog dialog1;
    private static AlertDialog dialog2;
    private static int pId = 0;

    public interface callResult {
        void result(boolean success, String result);
    }

    public static int getpId() {
        return pId;
    }

    public static void setpId(int pId) {
        Log.d(TAG, "hello :pId is " + pId);
        NetUtils.pId = pId;
    }

    public static void sendPost(final String r_url, final String data, final callResult callResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(r_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("content-type", "application/json; charset=utf-8");
                    conn.setReadTimeout(20000);
                    conn.setConnectTimeout(20000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os;
                    if (data != null) {
                        os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(data);
                        writer.flush();
                        writer.close();
                        os.close();
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            callResult.result(true, jsonObject.getString("data"));
                        } else {
                            callResult.result(false, jsonObject.getString("msg"));
                        }
                        in.close();
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    callResult.result(false, e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }


//    static String Base_Url = "http://192.168.0.108";
//    http://47.97.213.144/api/kwy_notify

    public static void Post(final String path, final Map<String, String> params, final callResult callResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(Base_Url + path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(20000);
                    conn.setConnectTimeout(20000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os;
                    os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(params));
                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            callResult.result(true, jsonObject.getString("data"));
                        } else {
                            callResult.result(false, jsonObject.getString("msg"));
                        }
                        in.close();
                    } else {
                        callResult.result(false, responseCode + "");
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    callResult.result(false, e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static final String PHONE = "KWY_PHONE";
    static final String PHONE_SAVE = "PHONE_SAVE";
    static final String PASSWD = "KWY_PASSWD";
    static final String PASSWD_SAVE = "PASSWD_SAVE";

    public static void savePhone(String phoneNumber, String passwd) {
        Log.d(TAG, "hello :savePhone phoneNumber is " + phoneNumber + " passwd is " + passwd);
        phoneNumber = phoneNumber.trim();
        passwd = passwd.trim();
        SpUtil.putString(PHONE, phoneNumber);
        SpUtil.putString(PASSWD, passwd);
    }

    public static boolean needUpdate() {
        String num = SpUtil.getString(PHONE);
        String pwd = SpUtil.getString(PASSWD);
        if (TextUtils.isEmpty(num) || TextUtils.isEmpty(pwd)) return false;

        String numSave = SpUtil.getString(PHONE_SAVE);
        String passwdSave = SpUtil.getString(PASSWD_SAVE);
        return !(numSave + passwdSave).equals(num + pwd);
    }

    public static void updateUser(String userId) {
        Log.d(TAG, "hello :updateUser userId is " + userId);
        saveUserId(userId);
        if (!needUpdate()) return;
        final String num = SpUtil.getString(PHONE);
        final String pwd = SpUtil.getString(PASSWD);

        HashMap<String, String> p = new HashMap<>();
        p.put("type", "login");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", num);
            jsonObject.put("password", pwd);
            jsonObject.put("userId", userId);
            p.put("data", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetUtils.Post("/data", p, new NetUtils.callResult() {
            @Override
            public void result(boolean success, final String result) {
                Log.d(TAG, success + " :" + result);
                if (success) {
                    SpUtil.putString(PHONE_SAVE, num);
                    SpUtil.putString(PASSWD_SAVE, pwd);
                }
            }
        });
    }


    private static String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String key : params.keySet()) {
            String value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));
        }
        return result.toString();
    }


    public static boolean isValidate(String id) {

        String code = SpUtil.getString("code" + id, "");
        String s = com.qennnsad.aknkaksd.hello.code.a(id);
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        String trim = m.replaceAll("").trim();
        trim = trim.substring(0, 4);

        boolean validate = code.equals(trim);


        Log.d(TAG, "hello : validate is " + validate);
        return validate;

//        return SpUtil.getBoolean("validate" + id, false);
    }

    public static boolean isValidate() {
        return isValidate(getUserId());
    }

    public static boolean isBreak() {
        return pId > 0;
//        return isValidate(getUserId());
    }

    public static void setValidate(String id) {
        if (TextUtils.isEmpty(id)) return;
        SpUtil.putString("code" + id, id);
    }

    public static Handler handler = new Handler();


    final static String USER_ID = "KWY_USER_ID";
    static String url = "";

    public static void resume(final Activity activity) {
        SpUtil.init(activity);
        updateUser(getUserId());
        String activityName = activity.getClass().getSimpleName();
        Log.d(TAG, "hello : " + activityName);
        if (activityName.contains("PlayerActivity")) {
            Log.d(TAG, "hello : on is PlayerActivity");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showInfo(activity);
                }
            }, 6000);
        }
    }


    public static void saveUserId(String userId) {
        SpUtil.putString(USER_ID, userId);
    }

    public static String getUserId() {
        return SpUtil.getString(USER_ID, "");
    }

    static final String SHOW_COUNT = "KWY_SHOW_COUNT";

    public static void showInfo(final Activity activity) {
        if (activity == null || activity.isDestroyed()) return;
        if (pId == 0) return;
        Log.d(TAG, "hello : showInfo is" + activity.getClass().getSimpleName());
        if (dialog1 != null && dialog1.isShowing()) return;
        if (!isValidate()) {
            int count = SpUtil.getInt(SHOW_COUNT, 0);
            count++;
            SpUtil.putInt(SHOW_COUNT, count);
            if (count < 2) return;

            sendPost("http://144.168.63.110:8888/getUrl", null, new callResult() {
                @Override
                public void result(boolean success, String result) {
                    if (success) {
                        url = result;
                        Log.d("geturl", url);
                    }
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("你好").setCancelable(false).
                    setMessage("破解程序试用结束了哦").setPositiveButton("激活", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showInputCode(activity);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                }
            });
            dialog1 = builder.show();
            WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
            params.height = 200;
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point outSize = new Point();
            display.getSize(outSize);
            params.height = (int) (outSize.y * 0.8);
            dialog1.getWindow().setAttributes(params);
        }
    }

    public static void showInputCode(final Activity activity) {
        if (dialog2 != null && dialog2.isShowing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText ed = new EditText(activity);
        builder.setTitle("输入激活码").setView(ed).setPositiveButton("激活", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setValidate(ed.getText().toString().trim());
                if (!isValidate()) {
                    Toast.makeText(activity, "激活码错误！", Toast.LENGTH_SHORT).show();
                    showInfo(activity);
                }
            }
        }).setNegativeButton("购买激活码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                if (TextUtils.isEmpty(url)) {
                    url = "https://github.com/wottert/HowTo/blob/master/custom.md";
                } else {
                    url = url + "?id=" + getUserId();
                }
                browserIntent.setData(Uri.parse(url));
                activity.startActivity(browserIntent);
            }
        });
        dialog2 = builder.show();
        dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showInfo(activity);
            }
        });
        WindowManager.LayoutParams params = dialog2.getWindow().getAttributes();
        params.height = 200;
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        params.height = (int) (outSize.y * 0.8);
        dialog2.getWindow().setAttributes(params);
    }
}
