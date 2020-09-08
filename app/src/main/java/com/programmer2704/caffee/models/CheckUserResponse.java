package com.programmer2704.caffee.models;

//09:14:00 edmt drink shop 1 2010 todo 25 JULI 2019 Kam
public class CheckUserResponse {
    private boolean exists;
    //todo ini harus sesuai nama variabel yang di php file
    private String error_msg;

    public CheckUserResponse() {

    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
