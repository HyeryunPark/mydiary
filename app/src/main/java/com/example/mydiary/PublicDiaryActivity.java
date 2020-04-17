package com.example.mydiary;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.mydiary.Adapter.PublicDiaryAdapter;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.model.DiaryModel;
import com.example.mydiary.sp.SharedMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.mydiary.sp.SharedMethod.getSPJA;

public class PublicDiaryActivity extends AppCompatActivity {

    ImageButton btn_closeOthersDiary, btn_bookMark;
    FloatingActionButton btn_makePublic;
    private ProgressDialog mProgressDialog;
    private BackgroundThread mBackThread;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;
    String nowUID;

    int current_year, current_month, current_dayOfMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_diary);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        nowUID = mAuth.getCurrentUser().getUid();    // 로그인한 현재 유저의 UID

        btn_closeOthersDiary = (ImageButton) findViewById(R.id.btn_closeOthersDiary);
        btn_bookMark = (ImageButton) findViewById(R.id.btn_bookMark);
        btn_makePublic = (FloatingActionButton) findViewById(R.id.btn_makePublic);

        // 리싸이클러뷰 적용
        mRecyclerView = findViewById(R.id.recyclerView_publicDiary);
        mRecyclerView.setHasFixedSize(true);    //  아이템이 보여지는것을 일정하게

        mLayoutManager = new LinearLayoutManager(this); // LinearLayoutManager를 사용한다.
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);   // 이 두줄 추가하면 추가한게 위로 붙는다.

        mRecyclerView.setLayoutManager(mLayoutManager);     // 앞에 선언한 리싸이클러뷰를 레이아웃매니저에 붙힌다.

        mAdapter = new PublicDiaryAdapter(this, btn_makePublic);
        mRecyclerView.setAdapter(mAdapter);

        // 현재날짜 구하기
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        current_month = month + 1;
        current_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final String current_date = current_year + ". " + current_month + ". " + current_dayOfMonth;

        // 쪼개서 배열에 넣고 합치기(키값으로 쓰려고 )
        String[] date = current_date.split(". ");
        String date_year = date[0];
        String date_month = date[1];
        String date_day = date[2];

        if (Integer.parseInt(date_month) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
            date_month = "0" + date_month;
        }
        if (Integer.parseInt(date_day) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
            date_day = "0" + date_day;
        }
        final String saveDate = date_year + date_month + date_day;


        // 우측 하단에 + 버튼을 누르면 오늘날짜의 내 글이 공개된다.
        btn_makePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder dig_askPublic = new AlertDialog.Builder(PublicDiaryActivity.this);
                dig_askPublic.setTitle("글 공개하기")
                        .setMessage("작성하신 오늘의 글을 공개하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 작성한 글이 있는지 없는지 판단하기 -> 글이 있어야 공유 가능
                                if (SharedMethod.selectDate2item(PublicDiaryActivity.this, current_date) == null) {
                                    // 오늘 날짜에 작성한 글이 없을때
                                    AlertDialog.Builder dig_noDiary = new AlertDialog.Builder(PublicDiaryActivity.this);
                                    dig_noDiary.setMessage("오늘 작성된 글이 없습니다.")
                                            .setPositiveButton("넹..", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog dialog_noDiary = dig_noDiary.create();
                                    dialog_noDiary.show();

                                } else if (SharedMethod.selectDate2item(PublicDiaryActivity.this, current_date) != null) {
                                    // 오늘 날짜에 작성한 글이 있을때
                                   /* //==================================================================================
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
                                    SharedPreferences publicDiary = getSharedPreferences("publicDiary", MODE_PRIVATE);
                                    // editor
                                    SharedPreferences.Editor editor = publicDiary.edit();

                                    // key : 로그인한 이메일, value : JSONObject
                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("날짜", current_date);
                                        ArrayList<Item_myDiary> item_myDiaries = getSPJA(v.getContext());
                                        for (Item_myDiary item : item_myDiaries) {

                                            if (item.getDiaryDate().equals(current_date)) {
                                                object.put("사진", item.diaryPicture);
                                                object.put("내용", item.diaryText);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("저장", String.valueOf(object));
                                    editor.putString(user_email, String.valueOf(object));
                                    editor.commit();*/


                                    // firebase저장
                                    Query query = mDatabase.getReference("Diarys").child(nowUID).child(saveDate);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            DiaryModel diaryModel = dataSnapshot.getValue(DiaryModel.class);
                                            mDatabase.getReference().child("PublicDiary").child(saveDate).child(nowUID).setValue(diaryModel);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                    // 오늘 일기 공개하면 + 아이콘 안보이게 하기
                                /*
                                     ( 28버전부터 플로팅버튼에 setVisibility를 사용하면 lint 에러가 뜬다.
                                     setVisivility를 직접 사용하지 않고 show(). hide()를 사용해야한다.
                                     fab.show(); - visible ,  fab.hide(); - gone
                                                                                                    */
                                    btn_makePublic.hide();
                                }

                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog_askPublic = dig_askPublic.create();
                dialog_askPublic.show();
            }
        });

        Log.d("일기공유", "onCreate()");


        mProgressDialog = ProgressDialog.show(PublicDiaryActivity.this, "", "데이터 불러오는중..");

        mBackThread = new BackgroundThread();
        mBackThread.setRunning(true);
        mBackThread.start();


    } //onCreate()

    public class BackgroundThread extends Thread {
        volatile boolean running = false;
        int cnt;

        void setRunning(boolean b) {
            running = b;
            cnt = 3;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    sleep(500);
                    if (cnt-- == 0) {
                        running = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(handler.obtainMessage());
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            boolean retry = true;
            while (retry) {
                try {
                    mBackThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("일기공유", "onStart()");
    } //onStart()

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("일기공유", "onResume()");
        // 다른이들의 오늘 창 닫기
        btn_closeOthersDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 내가 담아놓은 다른사람들 글 보는곳으로 이동
        btn_bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_bookmark = new Intent(PublicDiaryActivity.this, BookmarkActivity.class);
                startActivity(intent_bookmark);

            }
        });

    } //onResume()

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("일기공유", "onPause()");
    } //onPause()

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("일기공유", "onStop()");
    } //onStop()

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("일기공유", "onRestart()");
    } //onRestart()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("일기공유", "onDestroy()");
    } //onDestroy()


}
