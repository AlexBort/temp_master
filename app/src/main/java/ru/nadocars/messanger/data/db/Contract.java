package ru.nadocars.messanger.data.db;

import android.provider.BaseColumns;

//класс контракт содержит названия полей в таблицах базы
public final class Contract {

    public Contract() {
    }

    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_AVATAR_URL = "avatar_url";
        public static final String COLUMN_NAME_AVATAR_URI = "avatar_uri";
        public static final String COLUMN_NAME_TIME_OF_LAST_MESSAGE = "time_of_last_message";
        public static final String COLUMN_NAME_NUMBER_OF_NEW_MESSAGES = "number_of_new_messages";
        public static final String COLUMN_NAME_DIALOG_ID = "dialog_id";
        public static final String COLUMN_NAME_NULLABLE = "empty_or_null";
    }

    public static abstract class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_MESSAGE_ID = "message_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MESSAGE_TEXT = "text";
        public static final String COLUMN_NAME_AUTHOR_NAME = "name";
        public static final String COLUMN_NAME_IS_NEW_MESSAGE = "is_new_message";
        public static final String COLUMN_NAME_NULLABLE = "empty_or_null";
    }

}
