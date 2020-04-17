package com.example.mydiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ModifyDiaryActivity extends AppCompatActivity {

    ImageButton btn_writeCancel, btn_writeSave, btn_time, btn_choicePicture;
    ImageView iv_choicePicture;
    EditText et_writeDiary;
    TextView btn_choiceTag, tv_choiceDate;

    Calendar time = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", new Locale("en", "US"));

    String current_time, engDayOfWeek;
    int dayOfWeek;

    Bitmap img = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btn_writeCancel = (ImageButton) findViewById(R.id.btn_writeCancel);     // 글쓰기 취소
        tv_choiceDate = (TextView) findViewById(R.id.tv_choiceDate);            // 날짜 선택
        btn_writeSave = (ImageButton) findViewById(R.id.btn_writeSave);         // 쓴 글 저장

        iv_choicePicture = (ImageView) findViewById(R.id.iv_choicePicture);     // 선택한 사진 보이는 ImageView
        et_writeDiary = (EditText) findViewById(R.id.et_writeDiary);            // 일기 입력되는 곳

        btn_time = (ImageButton) findViewById(R.id.btn_time);                   // 시계 아이콘을 누르면 현재 시간,분 이 텍스트로 입력된다.
        btn_choicePicture = (ImageButton) findViewById(R.id.btn_choicePicture); // 카메라 아이콘을 누르면 사진을 업로드 할 수 있다.
        btn_choiceTag = (TextView) findViewById(R.id.btn_choiceTag);            // TAG 글씨를 눌러 현재 글의 TAG 색을 지정 할 수 있다.

        current_time = sdf.format(time.getTime());

        // 수정할 값들 가져오기
        Intent intent = getIntent();
        int position = intent.getExtras().getInt("position");
//        Item_myDiary item = Diary_items.get();



    }// onCreate()



}
