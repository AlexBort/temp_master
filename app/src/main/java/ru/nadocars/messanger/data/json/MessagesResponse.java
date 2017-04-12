package ru.nadocars.messanger.data.json;

//Класс в который преобразовываеться джейсон ответ сервера со списком сообщений
public class MessagesResponse {

    private int message_id;
    private int message_date;
    private String message_text;
    private String sender_name;
    private int is_new_message;

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getMessage_date() {
        return message_date;
    }

    public void setMessage_date(int message_date) {
        this.message_date = message_date;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getIs_new_message() {
        return is_new_message;
    }

    public void setIs_new_message(int is_new_message) {
        this.is_new_message = is_new_message;
    }
}
