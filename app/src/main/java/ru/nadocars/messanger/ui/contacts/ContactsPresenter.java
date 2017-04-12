package ru.nadocars.messanger.ui.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.List;

import ru.nadocars.messanger.data.json.DialogListResponse;
import ru.nadocars.messanger.ui.Presenter;

public interface ContactsPresenter extends Presenter<ContactsView> {

    void getContactsFromDb(Context context);
    void getContactsFromServer(Context context);

    void getUserAvatar(Context context);
    void setAvatar(Bitmap avatar);

    void logOut(Context context, String token);
    void showContacts(List<DialogListResponse> contacts);

    void getDialogAvatar(Context context, String url, ImageView avatarView);
    void clearDatabase();
    void startDialogsCheckService(Context context);
    void stopDialogsCheckService();

}
