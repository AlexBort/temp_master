package ru.nadocars.messanger.ui.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.asynctasks.GetContactsFromDbTask;
import ru.nadocars.messanger.asynctasks.GetContactsFromServerTask;
import ru.nadocars.messanger.asynctasks.GetDialogAvatarTask;
import ru.nadocars.messanger.asynctasks.GetUserAvatarTask;
import ru.nadocars.messanger.asynctasks.LogOutTask;
import ru.nadocars.messanger.asynctasks.SaveContactsToDbTask;
import ru.nadocars.messanger.data.json.DialogListResponse;
import ru.nadocars.messanger.data.repo.ContactsRepo;
import ru.nadocars.messanger.data.repo.MessagesRepo;
import ru.nadocars.messanger.domain.Contact;

//Класс - прослойка между интерфейсом и всем остальным
public class ContactsPresenterImpl implements ContactsPresenter {

    private ContactsView contactsView;
    private Timer timer;

    private static ContactsPresenterImpl contactsPresenter = new ContactsPresenterImpl();

    private ContactsPresenterImpl() {
    }

    public static ContactsPresenterImpl getContactsPresenter() {
        return contactsPresenter;
    }

    //колбэк чтобы получить ссылку на клас отвещающий за работу с экраном
    @Override
    public void setView(ContactsView view) {
        contactsView = view;
    }

    //получить контакты с базы
    @Override
    public void getContactsFromDb(Context context) {
        GetContactsFromDbTask getContactsFromDbTask = new GetContactsFromDbTask(contactsView, context);
        getContactsFromDbTask.execute();
    }

    //получить контакты с сервера
    @Override
    public void getContactsFromServer(Context context) {
        GetContactsFromServerTask getContactsFromServerTask = new GetContactsFromServerTask(context, this);
        getContactsFromServerTask.execute();
    }

    //логаут
    @Override
    public void logOut(Context context, String token) {
        LogOutTask logOutTask = new LogOutTask(context, token);
        logOutTask.execute();
    }

    //получить аватарку пользователя с сервера
    @Override
    public void getUserAvatar(Context context) {
        GetUserAvatarTask getUserAvatarTask = new GetUserAvatarTask(context, this);
        getUserAvatarTask.execute();
    }

    //отобразить контакты
    @Override
    public void showContacts(List<DialogListResponse> dialogs) {
        List<Contact> contacts = new ArrayList<>();
        Contact contact;
        for (DialogListResponse dialogListResponse : dialogs) {
            contact = new Contact();
            contact.setAvatarUrl(dialogListResponse.getAvatar());
            contact.setName(dialogListResponse.getFirst_name());
            contact.setTimeOfLastMessage(String.valueOf(dialogListResponse.getLast_message_date()));
            contact.setNumberOfNewMessages(dialogListResponse.getNew_message_count());
            contact.setDialogId(dialogListResponse.getDialog_id());
            contacts.add(contact);
        }
        saveDialogsToDb(contacts);
        contactsView.showContacts(contacts);
    }

    //отобразить аватарку
    @Override
    public void setAvatar(Bitmap avatar) {
        contactsView.setAvatar(avatar);
    }

    //получить аватарку для диалога
    @Override
    public void getDialogAvatar(Context context, String url, ImageView avatarView) {
        GetDialogAvatarTask getDialogAvatarTask = new GetDialogAvatarTask(context, avatarView, url);
        getDialogAvatarTask.execute();
    }

    //очистить базу
    @Override
    public void clearDatabase() {
        ContactsRepo contactsRepo = new ContactsRepo();
        contactsRepo.deleteAll();
        MessagesRepo messagesRepo = new MessagesRepo();
        messagesRepo.deleteAll();
    }

    //запустить службу проверки новых сообщений
    @Override
    public void startDialogsCheckService(final Context context) {
        final ContactsPresenter contactsPresenter = this;
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            GetContactsFromServerTask getContactsFromServerTask = new GetContactsFromServerTask(context, contactsPresenter);
                            getContactsFromServerTask.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 20000, getUpdateInterval());
    }

    //получить из найтроек интервал обновления
    private int getUpdateInterval() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contactsView.getContext());
        String checkNewMessagesInterval = sharedPreferences.getString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, null);
        if (checkNewMessagesInterval != null) {
            return Integer.valueOf(checkNewMessagesInterval) * 60000;
        } else {
            return 60000;
        }
    }

    //остановить службу проверки сообщений
    @Override
    public void stopDialogsCheckService() {
        timer.cancel();
    }

    //сохранить диалоги в базу
    private void saveDialogsToDb(List<Contact> contacts) {
        if (contactsView != null && contacts.size() > 0) {
            SaveContactsToDbTask saveContactsToDbTask = new SaveContactsToDbTask(contactsView.getContext(), contacts);
            saveContactsToDbTask.execute();
        }
    }

}
