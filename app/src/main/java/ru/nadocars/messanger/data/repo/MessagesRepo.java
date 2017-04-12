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
import ru.nadocars.messanger.domain.Message;

//работа с таблицей сообщений в базе
public class MessagesRepo {

    //сохранить всех
    public void insertAll(List<Message> messages, Context context) {
        DatabaseManager.initializeInstance(new DBHelper(context));
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        SQLiteDatabase db = databaseManager.openDatabase();
        db.delete(Contract.MessageEntry.TABLE_NAME, null, null);
        ContentValues values;
        for (ru.nadocars.messanger.domain.Message message : messages) {
            values = new ContentValues();
            values.put(Contract.MessageEntry.COLUMN_NAME_MESSAGE_ID, message.getMessageId());
            values.put(Contract.MessageEntry.COLUMN_NAME_DATE, message.getTime());
            values.put(Contract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT, message.getMessage());
            values.put(Contract.MessageEntry.COLUMN_NAME_AUTHOR_NAME, message.getAuthor());
            db.insert(Contract.MessageEntry.TABLE_NAME, Contract.MessageEntry.COLUMN_NAME_NULLABLE, values);
        }
        databaseManager.closeDatabase();
    }

    //удалить всех
    public void deleteAll() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        SQLiteDatabase db = databaseManager.openDatabase();
        db.delete(Contract.MessageEntry.TABLE_NAME, null, null);
        databaseManager.closeDatabase();
    }

    //получить всех
    public List<Message> findAllMessages() {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Contract.MessageEntry.TABLE_NAME, null);
        Message message;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            message = new Message();
            message.setAuthor(cursor.getString(cursor.getColumnIndex(Contract.MessageEntry.COLUMN_NAME_AUTHOR_NAME)));
            message.setMessage(cursor.getString(cursor.getColumnIndex(Contract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT)));
            message.setMessageId(cursor.getInt(cursor.getColumnIndex(Contract.MessageEntry.COLUMN_NAME_MESSAGE_ID)));
            message.setTime(cursor.getInt(cursor.getColumnIndex(Contract.MessageEntry.COLUMN_NAME_DATE)));
            message.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return messages;
    }

}
