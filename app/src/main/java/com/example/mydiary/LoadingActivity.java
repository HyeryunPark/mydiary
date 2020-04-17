package com.example.mydiary;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    TextView loading_tv1, loading_tv2;
    int p = 0;  // progressBar 값

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                mProgressBar.setProgress(p);

                if (p == 10) {
                    loading_tv1.setVisibility(View.VISIBLE);
                } else if (p == 40) {
                    loading_tv2.setVisibility(View.VISIBLE);

                } else if (p == 100) {
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                p++;
                handler.sendEmptyMessageDelayed(0, 10);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        loading_tv1 = (TextView) findViewById(R.id.loading_tv1);
        loading_tv2 = (TextView) findViewById(R.id.loading_tv2);

        handler.sendEmptyMessage(0);

    } //onCreate()


    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);
    }
}
