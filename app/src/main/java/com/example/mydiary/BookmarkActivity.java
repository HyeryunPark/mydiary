package com.example.mydiary;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mydiary.Adapter.BookmarkAdapter;
import com.example.mydiary.Item.Item_myDiary;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    public static ArrayList<Item_myDiary> Bookmark_items = new ArrayList<>();

    ImageButton btn_bookmarkClose;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    TextView tv_deletePDall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Log.d("Bookmark_items", "" + Bookmark_items.size());

        btn_bookmarkClose = (ImageButton) findViewById(R.id.btn_bookmarkClose);

        // 리싸이클러뷰 적용
        mRecyclerView = findViewById(R.id.recyclerView_bookmark);   // recyclerview id 가져오기
        mRecyclerView.setHasFixedSize(true);    // 아이템들이 보여지는것을 일정하게

        mLayoutManager = new LinearLayoutManager(this); // LinearLayoutManager를 사용한다.
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);   // 새로 추가한 아이템이 위로 추가되도록

        mRecyclerView.setLayoutManager(mLayoutManager);     // 앞에 선언한 리싸이클러뷰를 레이아웃매니저에 붙힌다.

        mAdapter = new BookmarkAdapter(Bookmark_items, this);
        mRecyclerView.setAdapter(mAdapter);





        // 북마크한 글들 전체삭제하는 코드 ( 일단 오류날때 다 지우려고 만들어논거니까 나중에 앱 완성하기 전에 지워. )
        tv_deletePDall = (TextView) findViewById(R.id.tv_deletePDall);
        tv_deletePDall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //==================================================================================
                // SharedPreferences
                // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
                SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                String AutoLogin = setting.getString("autoLogin", "");
                // 쪼개서 배열에 넣기
                String[] login_info = AutoLogin.split(",");
                String user_email = login_info[0];                  // 현재 로그인 되어있는 email
                String user_password = login_info[1];
                //==================================================================================
                // SharedPreferences에 저장하기
                // publicDiary.xml 이라는 이름의 SharedPreferences 파일을 가져온다 (없을 경우 자동생성)
                SharedPreferences publicDiary = getSharedPreferences("bookMark", MODE_PRIVATE);
                // editor
                SharedPreferences.Editor editor = publicDiary.edit();

                editor.clear();
                editor.commit();
            }
        });


        // 북마크 나가기
        btn_bookmarkClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }
}
