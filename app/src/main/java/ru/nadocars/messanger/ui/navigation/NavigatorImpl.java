package ru.nadocars.messanger.ui.navigation;

import android.content.Context;
import android.content.Intent;

import ru.nadocars.messanger.ui.contacts.ContactsActivity;
import ru.nadocars.messanger.ui.login.LoginActivity;

//Навигация
public class NavigatorImpl implements Navigator {

    //переход к контактам
    @Override
    public void navigateToContacts(Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        context.startActivity(intent);

    }

    //переход к логину
    @Override
    public void navigateToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }
}
