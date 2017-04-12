package ru.nadocars.messanger.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

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

import ru.nadocars.messanger.ConstantsApi;
import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.HttpApi;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.data.json.DialogListResponse;
import ru.nadocars.messanger.ui.contacts.ContactsActivity;

//Работает в фоне, проверяет новые сообщения
public class CheckNewMessagesService extends Service implements Runnable {

    private final Context context;
    private Thread myThread;

    public CheckNewMessagesService() {
        context = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //при создании запускает проверку в новом потоке
    @Override
    public void onCreate() {
        super.onCreate();
        myThread = new Thread(this);
        myThread.start();
    }

    //при уничтожении чистит хвосты
    @Override
    public void onDestroy() {
        super.onDestroy();
        myThread = null;
    }

    //получить интервал обновления
    private int getCheckNewMessagesIntervalInInMillis() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String checkNewMessagesInterval = sharedPreferences.getString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, null);
        if (checkNewMessagesInterval != null) {
            return Integer.valueOf(checkNewMessagesInterval) * 60000;
        } else {
            return 60000;
        }
    }

    //посылает широковещательное сообщение, что нужно обновить диалог
    private void sendUpdateDialogBroadcast(int dialogId) {
        Intent intent = new Intent(ConstantsApi.ACTION_UPDATE_DIALOG);
        intent.putExtra("dialogId", dialogId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //запускат в цикле проверку новых сообщений и в случае наличия отображение нотификейшена
    @Override
    public void run() {
        int checkNewMessagesIntervalInMillis = getCheckNewMessagesIntervalInInMillis();
        while (myThread == Thread.currentThread()) {
            try {
                Thread.sleep(checkNewMessagesIntervalInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<DialogListResponse> dialogs = new ArrayList<>();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo aNetworkInfo : networkInfo) {
                if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    URL getListOfDialogsUrl = null;
                    try {
                        getListOfDialogsUrl = new URL(HttpApi.BASE_URL + HttpApi.GET_DIALOGS_LIST);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (null != getListOfDialogsUrl) {
                        HttpURLConnection httpURLConnection = null;
                        try {
                            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            String token = defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null);
                            String urlParameters = "access_token=" + token;
                            byte[] postData = urlParameters.getBytes("UTF-8");
                            int postDataLength = postData.length;
                            httpURLConnection = (HttpURLConnection) getListOfDialogsUrl.openConnection();
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
            for (DialogListResponse dialogListResponse : dialogs) {
                if (dialogListResponse.getNew_message_count() > 0) {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setContentTitle("Сообщение от " + dialogListResponse.getFirst_name())
                            .setContentText(dialogListResponse.getLast_message_text());
                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    switch (audioManager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_VIBRATE:
                            notificationBuilder.setVibrate(new long[] { 1000, 1000 });
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            notificationBuilder.setVibrate(new long[] { 1000, 1000 });
                            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            break;
                    }
                    Intent intent = new Intent(context, ContactsActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(ContactsActivity.class);
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    int notificationId = 1;
                    mNotificationManager.notify(notificationId, notificationBuilder.build());
                    sendUpdateDialogBroadcast(dialogListResponse.getDialog_id());
                }
            }
        }
    }
}

