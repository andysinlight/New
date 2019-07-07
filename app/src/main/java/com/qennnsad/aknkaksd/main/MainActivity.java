package com.qennnsad.aknkaksd.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qennnsad.aknkaksd.hello.NetUtils;
import com.qennnsad.aknkaksd.hello.User;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        NetUtils.resume(this);

//        NetUtils.getState(this,id);
        NetUtils.updateUser("1234");
        NetUtils.savePhone("1234", "1234");
        NetUtils.updateUser("1234");
    }


    @Override
    protected void onResume() {
        super.onResume();
        NetUtils.resume(this);
        User user = new User();

        int a=1;

        NetUtils.setpId(a);


        if (NetUtils.isValidate()) {
            user = null;
        }

    }
}
