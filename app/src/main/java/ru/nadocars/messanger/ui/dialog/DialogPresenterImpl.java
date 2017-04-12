package ru.nadocars.messanger.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.asynctasks.GetMessagesFromDbTask;
import ru.nadocars.messanger.asynctasks.GetMessagesFromServerTask;
import ru.nadocars.messanger.asynctasks.LogOutTask;
import ru.nadocars.messanger.asynctasks.MessagesCheckTask;
import ru.nadocars.messanger.asynctasks.SaveMessagesToDb;
import ru.nadocars.messanger.asynctasks.SendMessageTask;
import ru.nadocars.messanger.data.json.MessagesResponse;
import ru.nadocars.messanger.data.repo.ContactsRepo;
import ru.nadocars.messanger.data.repo.MessagesRepo;
import ru.nadocars.messanger.domain.Message;

//Клас прослойка между экраном диалога и остальной "логикой"
public class DialogPresenterImpl implements DialogPresenter {

    private DialogView dialogView;
    private MessagesCheckTask messagesCheckTask;
    private Timer timer;
    private static DialogPresenterImpl dialogPresenter = new DialogPresenterImpl();

    private DialogPresenterImpl() {

    }

    public static DialogPresenterImpl getDialogPresenter() {
        return dialogPresenter;
    }

    @Override
    public void setView(DialogView view) {
        dialogView = view;
    }

    //получить сообщения с базы
    @Override
    public void getMessagesFromDb(Context context, int dialogId) {
        GetMessagesFromDbTask getMessagesFromDbTask = new GetMessagesFromDbTask(dialogView);
        getMessagesFromDbTask.execute();
    }

    //получить сообщения с сервера
    @Override
    public void getMessagesFromServer(Context context, int dialogId) {
        GetMessagesFromServerTask getMessagesFromServerTask = new GetMessagesFromServerTask(context, this, dialogId);
        getMessagesFromServerTask.execute();
    }

    //логаут
    @Override
    public void logOut(Context context, String token) {
        LogOutTask logOutTask = new LogOutTask(context, token);
        logOutTask.execute();
    }

    //отобразить сообщения
    @Override
    public void showMessages(List<MessagesResponse> messages) {
        List<Message> dialog = new ArrayList<>();
        Message message;
        for (MessagesResponse messagesResponse : messages) {
            message = new Message();
            message.setAuthor(messagesResponse.getSender_name());
            message.setMessage(messagesResponse.getMessage_text());
            message.setTime(messagesResponse.getMessage_date());
            message.setIsNewMessage(messagesResponse.getIs_new_message());
            message.setMessageId(messagesResponse.getMessage_id());
            dialog.add(message);
        }
        saveMessagesToDb(dialog);
        if (dialogView != null) {
            dialogView.showDialog(dialog);
        }
    }

    //почистить базу
    @Override
    public void clearDatabase() {
        ContactsRepo contactsRepo = new ContactsRepo();
        contactsRepo.deleteAll();
        MessagesRepo messagesRepo = new MessagesRepo();
        messagesRepo.deleteAll();
    }

    //сохранить сообщения в базу
    private void saveMessagesToDb(List<Message> messages) {
        if (dialogView != null & messages.size() > 0) {
            SaveMessagesToDb saveMessagesToDb = new SaveMessagesToDb(dialogView.getContext(), messages);
            saveMessagesToDb.execute();
        }
    }

    //запуск служби проверки сообщений
    @Override
    public void startMessagesCheckService(final Context context, final int dialogId) {
        final DialogPresenter dialogPresenter = this;
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            GetMessagesFromServerTask getMessagesFromServerTask = new GetMessagesFromServerTask(context, dialogPresenter, dialogId);
                            getMessagesFromServerTask.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 20000, getUpdateInterval());
        System.out.println(getUpdateInterval());
    }

    //остановка службы проверки сообщений
    @Override
    public void stopMessagesCheckService() {
        timer.cancel();
    }

    //получить интервал обновления
    private int getUpdateInterval() {
        SharedPreferences sharedPreferences;
        String checkNewMessagesInterval = "1";

        if (dialogView != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dialogView.getContext());
            checkNewMessagesInterval = sharedPreferences.getString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, null);
        }

        if (checkNewMessagesInterval != null) {
            return Integer.valueOf(checkNewMessagesInterval) * 60000;
        } else {
            return 60000;
        }
    }

    //отправить сообщение
    @Override
    public void sendMessage(Context context, String message, int dialogId) {
        SendMessageTask sendMessageTask = new SendMessageTask(context, message, this, dialogId);
        sendMessageTask.execute();
    }

    //отобразить ошибку ввода
    @Override
    public void showErrorWrongInput(String errorMessage) {
        dialogView.showErrorWrongInput(errorMessage);
    }
}
