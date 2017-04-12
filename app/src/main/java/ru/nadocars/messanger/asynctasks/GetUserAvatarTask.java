package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.contacts.ContactsPresenter;
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenter;

public class GetUserAvatarTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "GetUserAvatarTask";

    private final Context context;
    private  ConnectivityManager connectivityManager;
    private ContactsPresenter contactsPresenter;
    private UpdateLoginPresenter updateLoginPresenter;
    private Bitmap avatar;

    public GetUserAvatarTask(Context context, ContactsPresenter contactsPresenter) {
        this.context = context;
        this.contactsPresenter = contactsPresenter;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public GetUserAvatarTask(Context context, UpdateLoginPresenter updateLoginPresenter) {
        this.context = context;
        this.updateLoginPresenter = updateLoginPresenter;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //получает аватарку пользователя с сервера
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL getUserInfoUrl = null;
                try {
                    getUserInfoUrl = new URL(HttpApi.BASE_URL + HttpApi.GET_USER);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != getUserInfoUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                        String urlParameters = "access_token=" + token;
                        Log.e(TAG, "doInBackground: " + token);
                        byte[] postData = urlParameters.getBytes("UTF-8");
                        int postDataLength = postData.length;

                        httpURLConnection = (HttpURLConnection) getUserInfoUrl.openConnection();
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
                                avatar = downloadAvatar(new JSONObject(requestResult).getJSONObject("response").getString("avatar"));

                                Log.e(TAG, "doInBackground: " + avatar);
                                if (avatar != null) {
                                    isSuccessful = true;
                                }
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

    //сетит аватарку пользователя
    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            contactsPresenter.setAvatar(avatar);
//            updateLoginPresenter.setAvatar(avatar);
        }
    }

    //конкретно загрузка файла аватарки
    private Bitmap downloadAvatar(String avatarUrl) {
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            URL url = new URL(avatarUrl);
            URLConnection urlConnection = url.openConnection();
            inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            inputStream.close();
        } catch (IOException e) {
            Log.d("ImageManager", "Error: " + e);
        }
        return bitmap;
    }

}