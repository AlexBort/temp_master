package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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

import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.dialog.DialogPresenter;

public class SendMessageTask extends AsyncTask<Void, Void, Boolean> {

    private String errorMessage;
    private final Context context;
    private final String message;
    private final DialogPresenter dialogPresenter;
    private final int dialogId;
    private final ConnectivityManager connectivityManager;

    public SendMessageTask(Context context, String message, DialogPresenter dialogPresenter, int dialogId) {
        this.context = context;
        this.message = message;
        this.dialogPresenter = dialogPresenter;
        this.dialogId = dialogId;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //отправляет новое сообщение на сервер
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL loginUrl = null;
                try {
                    loginUrl = new URL(HttpApi.BASE_URL + HttpApi.DIALOG_POST);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != loginUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                        String urlParameters = "access_token=" + token + "&dialog_id=" + dialogId + "&message_text=" + message;
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
                                isSuccessful = true;
                            } else if (requestResult.contains("error")) {
                                JSONObject jsonObject = new JSONObject(requestResult);
                                JSONObject errorJsonObject = jsonObject.getJSONObject("error");
                                JSONObject errorDetailJsonObject = errorJsonObject.getJSONObject("error_detail");
                                errorMessage = errorDetailJsonObject.getString("message_text");
                                isSuccessful = false;
                            }
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

    //если отправка успешна - получает с сервера список сообщений, если нет, показывает ошибку.
    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            dialogPresenter.getMessagesFromServer(context, dialogId);
        } else {
            dialogPresenter.showErrorWrongInput(errorMessage);
        }
    }

}