package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.data.json.DialogListResponse;
import ru.nadocars.messanger.ui.contacts.ContactsPresenter;

// Этот клас достает достает контакты из сервера

public class GetContactsFromServerTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final ContactsPresenter contactsPresenter;
    private final List<DialogListResponse> dialogs;

    public GetContactsFromServerTask(Context context, ContactsPresenter contactsPresenter) {
        this.context = context;
        this.contactsPresenter = contactsPresenter;
        dialogs  = new ArrayList<>();
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //метод идет на сервер, получает список контактов и конвертирует из джейсона
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                URL listOfDialogs = null;
                try {
                    listOfDialogs = new URL(HttpApi.BASE_URL + HttpApi.GET_DIALOGS_LIST);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (null != listOfDialogs) {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                        String urlParameters = "access_token=" + token;
                        byte[] postData = urlParameters.getBytes("UTF-8");
                        int postDataLength = postData.length;

                        httpURLConnection = (HttpURLConnection) listOfDialogs.openConnection();
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
                                Gson gson = new Gson();
                                try {
                                    DialogListResponse dialogListResponse;
                                    JSONObject jsonObject = new JSONObject(requestResult);
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject currentJSONObject = jsonArray.getJSONObject(i);
                                        dialogListResponse = gson.fromJson(String.valueOf(currentJSONObject), DialogListResponse.class);
                                        dialogs.add(dialogListResponse);
                                    }
                                    isSuccessful = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
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

    //метод вызывает метод, который отображает контакты.
    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            contactsPresenter.showContacts(dialogs);
        }
    }

}