package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.login.LoginPresenter;
import ru.nadocars.messanger.ui.login.LoginPresenterImpl;
import ru.nadocars.messanger.ui.navigation.Navigator;
import ru.nadocars.messanger.ui.navigation.NavigatorImpl;

public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String email;
    private final String password;
    private final Context context;
    private final Navigator navigator;
    private final ConnectivityManager connectivityManager;
    private boolean isInternetConnected;
    private String requestResultMessage;
    private AppCompatActivity appCompatActivity;
    private String TAG = "UserLoginTask";

    public UserLoginTask(String email, String password, Context context, AppCompatActivity appCompatActivity) {
        this.email = email;
        this.password = password;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        navigator = new NavigatorImpl();
        isInternetConnected = false;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //логинимся на сервере
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                isInternetConnected = true;
                URL loginUrl = null;
                try {
                    loginUrl = new URL(HttpApi.BASE_URL + HttpApi.LOGIN);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != loginUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        String urlParameters = "email=" + email + "&password=" + password;
                        byte[] postData = urlParameters.getBytes("UTF-8");
                        int postDataLength = postData.length;

                        httpURLConnection = (HttpURLConnection) loginUrl.openConnection();
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
                        StringBuilder stringBuffer = new StringBuilder();
                        String data;
                        while ((data = reader.readLine()) != null) {
                            stringBuffer.append(data);
                        }
                        requestResult = stringBuffer.toString();
                        reader.close();

                        httpURLConnection.connect();
                        int status = httpURLConnection.getResponseCode();
                        if (status == 200) {
                            if (requestResult.contains("response")) {
                                JSONObject jsonObject = new JSONObject(requestResult);
                                JSONObject jsonObjectResponse = jsonObject.getJSONObject("response");
                                String accessToken = jsonObjectResponse.getString("access_token");

                                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                editor.putString(SharedPreferencesApi.TOKEN, accessToken);
                                editor.putString(SharedPreferencesApi.LOGIN, email);
                                editor.putString(SharedPreferencesApi.PASSWORD, password);
                                editor.apply();
                                isSuccessful = true;
                            } else if (requestResult.contains("error")) {
                                requestResultMessage = context.getResources().getString(R.string.error_wrong_login_or_password);
                            }
                        } else {
                            requestResultMessage = context.getResources().getString(R.string.error_server_error);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
            }
        }
        return isSuccessful;
    }

    //если успешно - переход к контактам, иначе показываем ошибки
    @Override
    protected void onPostExecute(final Boolean success) {
        LoginPresenter loginPresenter = LoginPresenterImpl.getLoginPresenter();
        loginPresenter.setAuthTask(null);
        loginPresenter.showProgress(false);
        if (!isInternetConnected) {
            Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();

        } else if (success) {
            navigator.navigateToContacts(context);
            appCompatActivity.finish();
        } else {
            loginPresenter.showError(requestResultMessage);
        }

    }

}