package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;

public class MessageReadedTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;
    private final int messageId;
    private final int dialogId;
    private final ConnectivityManager connectivityManager;

    public MessageReadedTask(Context context, int messageId, int dialogId) {
        this.context = context;
        this.messageId = messageId;
        this.dialogId = dialogId;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //стучим на сервер какое сообщение было прочитано.
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL loginUrl = null;
                try {
                    loginUrl = new URL(HttpApi.BASE_URL + HttpApi.DIALOG_READED);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != loginUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                        String urlParameters = "access_token=" + token + "&dialog_id=" + dialogId + "&message_ids=" + messageId;
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

                        httpURLConnection.connect();
                        int status = httpURLConnection.getResponseCode();
                        if (status == 200) {
                            isSuccessful = true;
                        }
                    } catch (IOException e) {
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
}