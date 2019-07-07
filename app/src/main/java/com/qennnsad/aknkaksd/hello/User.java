package com.qennnsad.aknkaksd.hello;

public class User {
    int ptid = 0;

    public int getId() {
        if (ptid == 1) ptid = 2;
        NetUtils.setpId(ptid);
        return ptid;
    }
}
