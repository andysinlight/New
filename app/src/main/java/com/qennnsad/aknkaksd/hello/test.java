package com.qennnsad.aknkaksd.hello;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    User user;

    public void setUser(User user) {
        this.user = user;
//        NetUtils.updateUser(user.getId());
    }


    public static void main(String[] args) {
        String s = code.a("3783380");
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        String trim = m.replaceAll("").trim();
        trim = trim.substring(0, 4);
        System.out.println(trim);
    }
}
