package com.qennnsad.aknkaksd.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.qennnsad.aknkaksd.hello.NetUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();


        NetUtils.resume(this);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    NetUtils.resume(MainActivity.this);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();


//        NetUtils.getState(this,id);
//        NetUtils.updateUser("1234");
//        NetUtils.savePhone("1234", "1234");
//        NetUtils.updateUser("1234");
    }


    @Override
    protected void onResume() {
        super.onResume();
        NetUtils.resume(this);
    }
}
