package ru.nadocars.messanger.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.nadocars.messanger.data.db.Contract;
import ru.nadocars.messanger.data.db.DBHelper;
import ru.nadocars.messanger.data.db.DatabaseManager;
import ru.nadocars.messanger.domain.Contact;

//работа с таблицей контактов в базе
public class ContactsRepo {

    //сохранить всех
    public void insertAll(List<Contact> contacts, Context context) {
        DatabaseManager.initializeInstance(new DBHelper(context));
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        SQLiteDatabase db = databaseManager.openDatabase();
        db.delete(Contract.ContactEntry.TABLE_NAME, null, null);
        ContentValues values;
        for (Contact contact : contacts) {
            values = new ContentValues();
            values.put(Contract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
            values.put(Contract.ContactEntry.COLUMN_NAME_AVATAR_URL, contact.getAvatarUrl());
            values.put(Contract.ContactEntry.COLUMN_NAME_TIME_OF_LAST_MESSAGE, contact.getTimeOfLastMessage());
            values.put(Contract.ContactEntry.COLUMN_NAME_NUMBER_OF_NEW_MESSAGES, contact.getNumberOfNewMessages());
            values.put(Contract.ContactEntry.COLUMN_NAME_DIALOG_ID, contact.getDialogId());
            db.insert(Contract.ContactEntry.TABLE_NAME, Contract.ContactEntry.COLUMN_NAME_NULLABLE, values);
        }
        databaseManager.closeDatabase();
    }

    //удалить всех
    public void deleteAll() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        SQLiteDatabase db = databaseManager.openDatabase();
        db.delete(Contract.ContactEntry.TABLE_NAME, null, null);
        databaseManager.closeDatabase();
    }

    //получить всех
    public List<Contact> findAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Contract.ContactEntry.TABLE_NAME, null);
        ru.nadocars.messanger.domain.Contact contact;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            contact = new Contact();
            contact.setName(cursor.getString(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_NAME)));
            contact.setAvatarUrl(cursor.getString(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_AVATAR_URL)));
            contact.setDialogId(cursor.getInt(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_DIALOG_ID)));
            contact.setNumberOfNewMessages(cursor.getInt(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_NUMBER_OF_NEW_MESSAGES)));
            contact.setAvatarUri(cursor.getString(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_AVATAR_URI)));
            contact.setTimeOfLastMessage(cursor.getString(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_TIME_OF_LAST_MESSAGE)));
            contact.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            contacts.add(contact);
            cursor.moveToNext();
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return contacts;
    }

    //найти контакт по полю "урл"
    public Contact findContactByUrl(String url) {
        Contact contact = new ru.nadocars.messanger.domain.Contact();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Contract.ContactEntry.COLUMN_NAME_AVATAR_URL + ", _id" + " FROM " + Contract.ContactEntry.TABLE_NAME + " WHERE " + Contract.ContactEntry.COLUMN_NAME_AVATAR_URL + " = \"" + url + "\"", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String avatarUrl = cursor.getString(cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME_AVATAR_URL));
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            contact.setAvatarUrl(avatarUrl);
            contact.setId(id);
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return contact;
    }

}
