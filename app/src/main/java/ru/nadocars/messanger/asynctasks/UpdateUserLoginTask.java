package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.ToastForTask;
import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.login.LoginPresenter;
import ru.nadocars.messanger.ui.login.LoginPresenterImpl;
import ru.nadocars.messanger.ui.profile.ProfileActivity;
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenter;
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenterImpl;

/**
 * Created by User on 14.12.2016.
 */


public class UpdateUserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "UpdateUserLoginTask";
    private String email;
    private String phoneNumber;
    String token;

    String getCodeSMS;
    String getSessionID;

    private final Context context;

    private AppCompatActivity appCompatActivity;
    private final ConnectivityManager connectivityManager;
    private boolean isInternetConnected;
    private String requestResultMessage;


    public UpdateUserLoginTask(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity) {
        isInternetConnected = false;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.context = context;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.appCompatActivity = appCompatActivity;
    }


    //логинимся на сервере
    @Override
    protected Boolean doInBackground(Void... params) {

        Log.e(TAG, "doInBackground: " + "Заходит в UpdateUserLoginTask");

        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                isInternetConnected = true;
                HttpURLConnection httpURLConnection = null;
                URL currentUserLogin = null;
                try {
                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);

                    currentUserLogin = new URL(HttpApi.BASE_URL + HttpApi.USER_UPDATE);


                    String urlParameters = "access_token=" + token + "&email=" + email + "&phone=";

                    String parametersPhone = phoneNumber;


                    String urlEncoderPhone = URLEncoder.encode(parametersPhone, "UTF-8");
                    Log.e(TAG, "doInBackground: " + urlEncoderPhone);

                    String finalUrlParameters = urlParameters + urlEncoderPhone;
                    Log.e(TAG, "doInBackground: " + finalUrlParameters);


                    byte[] postData = finalUrlParameters.getBytes("UTF-8");
                    int postDataLength = postData.length;

                    httpURLConnection = (HttpURLConnection) currentUserLogin.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(false);
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("charset", "utf-8");
                    httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    httpURLConnection.setUseCaches(false);

                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(postData);
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String requestResult;
                    StringBuilder stringBuilder = new StringBuilder();
                    String data;
                    while ((data = reader.readLine()) != null) {
                        stringBuilder.append(data);
                    }
                    requestResult = stringBuilder.toString();
                    reader.close();

                    httpURLConnection.connect();

                    int status = httpURLConnection.getResponseCode();
                    Log.e(TAG, "doInBackground: status " + status);
                    if (status == 200) {


                        if (requestResult.equals("1")) {
                            Log.e(TAG, "doInBackground: " + "successfully!");
                            isSuccessful = true;
                            ToastForTask.printToast(TAG, "Адрес успешно обновлён");
                        }

                        if (requestResult.contains("error")) {
                            getCodeSMS = new JSONObject(requestResult).getJSONObject("error").getJSONObject("error_detail").getString("code");
                            getSessionID = new JSONObject(requestResult).getJSONObject("error").getJSONObject("error_detail").getString("session_id");



                            if (getCodeSMS != null && getSessionID != null) {
                                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                editor.putString(SharedPreferencesApi.SESSION_ID, getSessionID);
                                editor.putString(SharedPreferencesApi.CODE_SMS, getCodeSMS);
                                editor.apply();

                                Log.e(TAG, "doInBackground: " + "error");
                            }

                        }


                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }


                }

            }

        }
        return isSuccessful;
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        UpdateLoginPresenter updateLoginPresenter = UpdateLoginPresenterImpl.getUpdateLoginPresenter();
        updateLoginPresenter.setUpdateUserLoginTask(null);
        updateLoginPresenter.showProgress(false);
        if (!isInternetConnected) {
            Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onPostExecute: INTERNET " + "ЗАХОДИТ В ЭТОТ БЛОК");
        }

    }


}
