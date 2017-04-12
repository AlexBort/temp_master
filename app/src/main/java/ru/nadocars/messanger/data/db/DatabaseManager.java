package ru.nadocars.messanger.data.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Класс для получения доступа к базе
public class DatabaseManager {
    private Integer mOpenCounter = 0;

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    //инициализация
    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    //метод возвращает обьект этого класа
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    //открыть конект к базе
    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter += 1;
        if (mOpenCounter == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    //закрыть коннект к базе
    public synchronized void closeDatabase() {
        mOpenCounter -= 1;
        if (mOpenCounter == 0) {
            mDatabase.close();
        }
    }
}