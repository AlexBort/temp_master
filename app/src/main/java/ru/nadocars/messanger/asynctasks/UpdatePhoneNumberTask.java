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
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenter;
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenterImpl;

/**
 * Created by User on 21.12.2016.
 */

public class UpdatePhoneNumberTask extends AsyncTask<Void, Void, Boolean> {

    String codeSMS;
    String sessionID;

    private boolean statusChanged = true;

    String vvod;

    private static final String TAG = "UpdatePhoneNumberTask";
    String token;

    private String requestResultMessage;


    String email;
    String phoneNumber;
    private AppCompatActivity appCompatActivity;
    private final ConnectivityManager connectivityManager;
    private Context context;
    private boolean isInternetConnected;

    public UpdatePhoneNumberTask(String email, String phoneNumber, Context context,
                                 AppCompatActivity appCompatActivity, String codeSMS) {
        isInternetConnected = false;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.context = context;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.appCompatActivity = appCompatActivity;
        this.codeSMS = codeSMS;

    }


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
                    sessionID = defaultSharedPreferences.getString(SharedPreferencesApi.SESSION_ID, null);
                    codeSMS = defaultSharedPreferences.getString(SharedPreferencesApi.CODE_SMS, null);
                    Log.e(TAG, "doInBackground: codeSMS in  UpdatePhoneNumberTask" + codeSMS);


                    currentUserLogin = new URL(HttpApi.BASE_URL + HttpApi.USER_UPDATE);


                    String urlParameters = "access_token=" + token + "&email=" + email + "&phone=";


                    String urlEncoderPhone = URLEncoder.encode(phoneNumber, "UTF-8");

                    String otherParameters = "&session_id=" + sessionID + "&code=" + codeSMS;

                    Log.e(TAG, "doInBackground: " + urlEncoderPhone);

                    String finalUrlParameters = urlParameters + urlEncoderPhone + otherParameters;
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
                       //     ToastForTask.printToast(TAG, "Телефонный номер успешно обновлён");
                            isSuccessful = true;
                        } else if (requestResult.contains("error")) {

                            Log.e(TAG, "doInBackground: " + "error");

                            String catchError = new JSONObject(requestResult).getJSONObject("error").getString("error_msg");
                            Log.e(TAG, "doInBackground: text of ERROR: " + catchError);
                            ToastForTask.printToast(TAG,catchError);

                        } else {
                            ToastForTask.printToast(TAG, "Ошибка сервера");
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
    protected void onPostExecute(Boolean aBoolean) {
        UpdateLoginPresenter updateLoginPresenter = UpdateLoginPresenterImpl.getUpdateLoginPresenter();
        updateLoginPresenter.setUpdateUserLoginTask(null);
        updateLoginPresenter.setUpdatePhoneNumberTask(null);
        updateLoginPresenter.showProgress(false);
        if (!isInternetConnected) {
            Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onPostExecute: " + "ЗАХОДИТ В ЭТОТ БЛОК");

        }
    }


}
