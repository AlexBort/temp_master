package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.nadocars.messanger.api.HttpApi;

public class LogOutTask extends AsyncTask<Void, Void, Boolean> {

    private final String token;
    private final ConnectivityManager connectivityManager;

    public LogOutTask(Context context, String token) {
        this.token = token;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //шлум на сервер что мы логаутимся.
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL loginUrl = null;
                try {
                    loginUrl = new URL(HttpApi.BASE_URL + HttpApi.LOGOUT);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != loginUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        String urlParameters = "access_token=" + token;
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
                        isSuccessful = true;
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