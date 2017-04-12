package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.dialog.DialogPresenter;

public class MessagesCheckTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;

    private final DialogPresenter dialogPresenter;
    private int dialogId;
    private Thread myThread = null;

    public MessagesCheckTask(Context context, DialogPresenter dialogPresenter, int dialogId) {
        this.context = context;
        this.dialogPresenter = dialogPresenter;
        this.dialogId = dialogId;
    }

    //берет установленый интервал и запускает получение сообщений с сервера.
    @Override
    protected Boolean doInBackground(Void... params) {
        myThread = Thread.currentThread();
        final int interval;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String checkNewMessagesInterval = sharedPreferences.getString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, null);
        if (checkNewMessagesInterval != null) {
            interval = Integer.valueOf(checkNewMessagesInterval) * 60000;
        } else {
            interval = 600000;
        }

        while (myThread == Thread.currentThread()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialogPresenter.getMessagesFromServer(context, dialogId);
        }


        return false;
    }

    @Override
    protected void onCancelled() {
        myThread = null;
        super.onCancelled();
    }

}