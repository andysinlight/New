package com.qennnsad.aknkaksd.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qennnsad.aknkaksd.hello.NetUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        NetUtils.resume(this);

//        NetUtils.getState(this,id);
        NetUtils.updateUser("123456");
        NetUtils.savePhone("123456", "1234");
    }
}
