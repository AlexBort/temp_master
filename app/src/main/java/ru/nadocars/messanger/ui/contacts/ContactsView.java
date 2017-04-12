package ru.nadocars.messanger.ui.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.List;

import ru.nadocars.messanger.domain.Contact;

public interface ContactsView {

    void showContacts(List<Contact> contacts);
    void setAvatar(Bitmap avatar);
    void getAndSetDialogAvatar(String url, ImageView avatarView);
    String saveDialogAvatar(Bitmap avatar);
    Context getContext();

}
