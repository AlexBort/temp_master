package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.util.List;

import ru.nadocars.messanger.data.repo.ContactsRepo;
import ru.nadocars.messanger.domain.Contact;
import ru.nadocars.messanger.ui.contacts.ContactsView;


/*
* Этот клас достает контакты с базы
 */

public class GetContactsFromDbTask extends AsyncTask<Void, Void, Boolean> {

    private final ContactsView contactsView;
    private final Context context;
    private List<Contact> contacts;

    public GetContactsFromDbTask(ContactsView contactsView, Context context) {
        this.contactsView = contactsView;
        this.context = context;
    }

    // метод достает из базы контакты и аватарки к ним.
    @Override
    protected Boolean doInBackground(Void... params) {
        ContactsRepo contactsRepo = new ContactsRepo();
        contacts = contactsRepo.findAllContacts();
        if (contacts.size() > 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            for (Contact contact : contacts) {
                contact.setAvatarUri(sharedPreferences.getString(contact.getAvatarUrl(), null));
            }
        }
        return false;
    }

    // метод вызывает другой метод, который отображает контакты
    @Override
    protected void onPostExecute(final Boolean success) {
        if (contacts.size() > 0) {
            contactsView.showContacts(contacts);
        }
    }

}
