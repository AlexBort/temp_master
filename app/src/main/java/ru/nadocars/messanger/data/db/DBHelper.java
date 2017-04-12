package ru.nadocars.messanger.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Клас для создания таблиц в базе
public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Chat.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + Contract.ContactEntry.TABLE_NAME + " (" +
                    Contract.ContactEntry._ID + " INTEGER PRIMARY KEY," +
                    Contract.ContactEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    Contract.ContactEntry.COLUMN_NAME_AVATAR_URL + TEXT_TYPE + COMMA_SEP +
                    Contract.ContactEntry.COLUMN_NAME_AVATAR_URI + TEXT_TYPE + COMMA_SEP +
                    Contract.ContactEntry.COLUMN_NAME_TIME_OF_LAST_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    Contract.ContactEntry.COLUMN_NAME_NUMBER_OF_NEW_MESSAGES + INTEGER_TYPE + COMMA_SEP +
                    Contract.ContactEntry.COLUMN_NAME_DIALOG_ID + INTEGER_TYPE +
            " )";
    private static final String SQL_CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + Contract.MessageEntry.TABLE_NAME + " (" +
                    Contract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
                    Contract.MessageEntry.COLUMN_NAME_MESSAGE_ID + INTEGER_TYPE + COMMA_SEP +
                    Contract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT + TEXT_TYPE + COMMA_SEP +
                    Contract.MessageEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    Contract.MessageEntry.COLUMN_NAME_AUTHOR_NAME + TEXT_TYPE + COMMA_SEP +
                    Contract.MessageEntry.COLUMN_NAME_IS_NEW_MESSAGE + BOOLEAN_TYPE +
                    " )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}