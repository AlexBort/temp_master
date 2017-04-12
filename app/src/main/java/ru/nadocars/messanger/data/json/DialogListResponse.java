package ru.nadocars.messanger.data.json;

//Класс в который преобразовываеться джейсон ответ сервера со списком диалогов
public class DialogListResponse {

    private int dialog_id;
    private int user_id;
    private String last_name;
    private String first_name;
    private String middle_name;
    private String avatar;
    private int last_message_id;
    private int last_message_date;
    private String last_message_text;
    private int new_message_count;

    public int getDialog_id() {
        return dialog_id;
    }

    public void setDialog_id(int dialog_id) {
        this.dialog_id = dialog_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(int last_message_id) {
        this.last_message_id = last_message_id;
    }

    public int getLast_message_date() {
        return last_message_date;
    }

    public void setLast_message_date(int last_message_date) {
        this.last_message_date = last_message_date;
    }

    public String getLast_message_text() {
        return last_message_text;
    }

    public void setLast_message_text(String last_message_text) {
        this.last_message_text = last_message_text;
    }

    public int getNew_message_count() {
        return new_message_count;
    }

    public void setNew_message_count(int new_message_count) {
        this.new_message_count = new_message_count;
    }
}
