package com.tarun.SpringProject.SpringProject.Entity;

import java.util.ArrayList;

public class Messages {

    String sendername;

    ArrayList<String> message = new ArrayList<>();

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }
}
