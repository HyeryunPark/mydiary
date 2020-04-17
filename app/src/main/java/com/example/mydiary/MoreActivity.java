package com.example.mydiary;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class MoreActivity extends AppCompatActivity {

    ImageButton btn_closeMore;
    TextView tv_logout,tv_notificationSetting, tv_deleteDiaryAll;

    Calendar cal = Calendar.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        final String nowUID = mAuth.getCurrentUser().getUid();    // 로그인한 현재 유저의 UID

        btn_closeMore = (ImageButton) findViewById(R.id.btn_closeMore);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_notificationSetting = (TextView) findViewById(R.id.tv_notificationSetting);
        tv_deleteDiaryAll = (TextView) findViewById(R.id.tv_deleteDiaryAll);

        // 왼쪽상단에 뒤로 돌아가는 버튼
        btn_closeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 로그아웃
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dig_logout = new AlertDialog.Builder(MoreActivity.this);
                dig_logout.setTitle("로그아웃")
                        .setMessage("정말 로그아웃하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = setting.edit();
                                autoLogin.remove("autoLogin");
                                autoLogin.commit();

                                Intent intent_logout = new Intent(MoreActivity.this, LoginActivity.class);
                                startActivity(intent_logout);
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog_logout = dig_logout.create();
                dialog_logout.show();
            }
        });

        // 알림을 설정한다. (매일 정해진 시간에 알림을 받아서 일기를 쓰도록 도와준다.)
        tv_notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(MoreActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

                        String msg = String.format("%d 시 %d 분", hour, min);
                        Toast.makeText(MoreActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

                dialog.show();


            }
        });

        // 그동안 작성한 모든 일기들을 삭제한다.
        tv_deleteDiaryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dig_deleteDiaryAll = new AlertDialog.Builder(MoreActivity.this);
                dig_deleteDiaryAll.setTitle("모든 글 삭제")
                        .setMessage("그동안 작성한 모든 일기를 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                                // diary.xml 이라는 이름의 SharedPreferences 파일을 가져온다 (없을 경우 자동생성)
                                SharedPreferences diary = getSharedPreferences("diary", MODE_PRIVATE);
                                // editor
                                SharedPreferences.Editor editor = diary.edit();

                                editor.clear();
                                editor.commit();




                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog_deleteDiaryAll = dig_deleteDiaryAll.create();
                dialog_deleteDiaryAll.show();

            }
        });




    } //onCreate()

    @Override
    protected void onStart() {
        super.onStart();

    } //onStart()

    @Override
    protected void onResume() {
        super.onResume();

    } //onResume()

    @Override
    protected void onPause() {
        super.onPause();

    } //onPause()

    @Override
    protected void onStop() {
        super.onStop();

    } //onStop()

    @Override
    protected void onRestart() {
        super.onRestart();

    } //onRestart()

    @Override
    protected void onDestroy() {
        super.onDestroy();

    } //onDestroy()


}
