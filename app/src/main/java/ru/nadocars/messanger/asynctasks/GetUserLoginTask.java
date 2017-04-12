package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
import ru.nadocars.messanger.ui.profile.UpdateLoginPresenter;

/**
 * Created by User on 14.12.2016.
 */


public class GetUserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "GetUserLoginTask";
    private String email;
    private String phoneNumber;
    private String statusOfUser;

    private String personalBalance; // TODO: 31.12.2016 в каких единициах БАЛАНС делать
    private Bitmap avatar;
    private String lastName;
    private String firstName;
    private String middleName;


    private String userOwner;
    private String userRenter;
    String token;

    private final Context context;

    private final UpdateLoginPresenter updateLoginPresenter;

    private final ConnectivityManager connectivityManager;
    private boolean isInternetConnected;
    private String requestResultMessage;
    private AppCompatActivity appCompatActivity;


    public GetUserLoginTask(UpdateLoginPresenter updateLoginPresenter, Context context) {
        this.updateLoginPresenter = updateLoginPresenter;
        this.context = context;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    //логинимся на сервере
    @Override
    protected Boolean doInBackground(Void... params) {

        Log.e(TAG, "doInBackground: " + "ЗАХОДИТ!!");

        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL getUserInfoUrl = null;
                try {

                    // Метод для получения инфы о пользователе!
                    getUserInfoUrl = new URL(HttpApi.BASE_URL + HttpApi.GET_USER);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != getUserInfoUrl) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                        // посылаем token запрос
                        token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                        String urlParameters = "access_token=" + token;
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





                        // ПОЛУЧАЕМ ОТВЕТ НА ЭТОТ ЗАПРОС
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        String requestResult;
                        StringBuilder stringBuffer = new StringBuilder();
                        String data;
                        while ((data = reader.readLine()) != null) {
                            stringBuffer.append(data);
                        }
                        requestResult = stringBuffer.toString();

                        Log.e(TAG, "doInBackground: " + requestResult);
                        reader.close();

                        httpURLConnection.connect();
                        int status = httpURLConnection.getResponseCode();
                        if (status == 200) {
                            if (requestResult.contains("response")) {
                                Log.e(TAG, "doInBackground: " + "в requestResult заходит");
                                email = new JSONObject(requestResult).getJSONObject("response").getString("email");
                                Log.e(TAG, "doInBackground: " + email);

                                phoneNumber = new JSONObject(requestResult).getJSONObject("response").getString("phone");

                                userOwner = new JSONObject(requestResult).getJSONObject("response").getString("is_owner");
                                userRenter = new JSONObject(requestResult).getJSONObject("response").getString("is_renter");
                                personalBalance = new JSONObject(requestResult).getJSONObject("response").getString("balance");

                               avatar = downloadAvatar(new JSONObject(requestResult).getJSONObject("response").getString("avatar"));

                                firstName = new JSONObject(requestResult).getJSONObject("response").getString("first_name");
                                middleName = new JSONObject(requestResult).getJSONObject("response").getString("middle_name");
                                lastName = new JSONObject(requestResult).getJSONObject("response").getString("last_name");


                                Log.e(TAG, "doInBackground: LAST_NAME " + lastName);
                                Log.e(TAG, "doInBackground: BALANCE " + personalBalance);

                                Log.e(TAG, "doInBackground: value of Renter" + userRenter);
                                Log.e(TAG, "doInBackground: value of Owner" + userOwner);

                                Log.e(TAG, "doInBackground: AVATAR" + avatar);
                                Log.e(TAG, "doInBackground: BALANCE" + personalBalance);
                                // is_owner
                                // is_renter


                                Log.e(TAG, "doInBackground: " + phoneNumber);
                                if (email != null && phoneNumber != null) {
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


    @Override
    protected void onPostExecute(final Boolean success) {

        updateLoginPresenter.setCurrentMail(email);
        updateLoginPresenter.setCurrentPhoneNumber(phoneNumber);
        updateLoginPresenter.setPersonalBalance(personalBalance);

        updateLoginPresenter.setFirstName(firstName);
        updateLoginPresenter.setMiddleName(middleName);
        updateLoginPresenter.setLastName(lastName);
        updateLoginPresenter.setAvatar(avatar);


     /*   updateLoginPresenter.getCurrentPhoneNumber(phoneNumber);
        updateLoginPresenter.getCurrentMail(email);*/

    }

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