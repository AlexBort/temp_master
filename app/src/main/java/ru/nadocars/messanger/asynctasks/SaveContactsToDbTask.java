package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import ru.nadocars.messanger.data.repo.ContactsRepo;
import ru.nadocars.messanger.domain.Contact;

public class SaveContactsToDbTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private List<Contact> contacts;

    public SaveContactsToDbTask(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    //Сохраняет контакты в базу
    @Override
    protected Boolean doInBackground(Void... params) {
        ContactsRepo contactsRepo = new ContactsRepo();
        contactsRepo.insertAll(contacts, context);
        return false;
    }


}
