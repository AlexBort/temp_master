package ru.nadocars.messanger;

import android.app.Application;

import ru.nadocars.messanger.data.db.DBHelper;
import ru.nadocars.messanger.data.db.DatabaseManager;

//Главный клас приложения
public class MyApplication extends Application {

    //При создании инициализируем базу
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseManager.initializeInstance(new DBHelper(this));
    }
}
