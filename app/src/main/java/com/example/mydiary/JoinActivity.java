package com.example.mydiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydiary.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class JoinActivity extends AppCompatActivity {

    EditText edit_joinName, edit_joinEmail, edit_joinPassword, edit_joinPasswordCk;
    Button btn_joinOk;
    long currentTime;   // 액티비티가 닫혔을때의 시간
    long timeOut;   // 액티비티를 종료한 시간과 현재 시간의 차이

    // 파이어베이스
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.d("회원가입액티비티", "=====> onCreate 호출됨");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        edit_joinName = (EditText) findViewById(R.id.edit_joinName);                // 이름 입력란
        edit_joinEmail = (EditText) findViewById(R.id.edit_joinEmail);              // 이메일 입력란
        edit_joinPassword = (EditText) findViewById(R.id.edit_joinPassword);        // 비밀번호 입력란
        edit_joinPasswordCk = (EditText) findViewById(R.id.edit_joinPasswordCk);    // 비밀번호 확인 입력란
        btn_joinOk = (Button) findViewById(R.id.btn_joinOk);                        // 회원가입 버튼

        // 회원가입
        // 회원가입하는 양식들 중 빈칸이 있는지 체크하고 있으면 회원가입 불가 - ok
        // 비밀번호와 비밀번호 확인란에 입력된 값이 같은지 확인. 다르면 다르다고 토스트 나오고 회원가입 불가 - ok
        // 이메일형식, 비밀번호 형식(영문자, 특수문자, 숫자) 유효성 검사까지 하기 -
        // 비밀번호 6자리 이상
        btn_joinOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = edit_joinPassword.getText().toString();
                String pw2 = edit_joinPasswordCk.getText().toString();

                // 회원가입 양식에 빈칸이 없으면
                if (edit_joinName.getText().toString().length() != 0 && edit_joinEmail.getText().toString().length() != 0 && edit_joinPassword.getText().toString().length() != 0) {

                    // 비밀번호 6자리 이상 입력하도록
                    if (edit_joinPassword.getText().length() >= 6) {


                        // 비밀번호, 비밀번호 확인이 일치하는지 체크
                        if (pw.equals(pw2)) { // 일치하면 입력값 저장하고 회원가입 성공


                            // SharedPreferences
                            // join.xml 이라는 이름의 SharedPreferences 파일을 가져온다 (없을 경우 자동생성)
                            SharedPreferences join = getSharedPreferences("join", MODE_PRIVATE);
                            // editor
                            SharedPreferences.Editor editor = join.edit();
                            // key값 : 이메일 , value값 : 이름, 비밀번호
                            editor.putString(edit_joinEmail.getText().toString(), edit_joinName.getText().toString() + "," + edit_joinPassword.getText().toString());
                            editor.commit();
                            // 회원가입이 완료되었다는 메세지 띄우기
                            Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("저장된 회원정보 ============>", join.getString(edit_joinEmail.getText().toString(), ""));


                            createUser(edit_joinEmail.getText().toString(), edit_joinPassword.getText().toString());




                        } else {    // 불일치하면 토스트 띄우고 회원가입 실패
                            Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(JoinActivity.this, "비밀번호 6자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(JoinActivity.this, "회원가입 양식을 채워 주세요", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }   //onCreate()

    // 회원가입 코드
    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {  // 회원가입 성공
                            Log.d("파베 회원가입", "createUserWithEmail:success");
                            Toast.makeText(JoinActivity.this, "회원가입 성공성공성공.", Toast.LENGTH_SHORT).show();

                            // 회원가입성공시 데이터 베이스에 정보 넣기.
                            UserModel userModel = new UserModel();
                            userModel.userName = edit_joinName.getText().toString();
                            userModel.userEmail = edit_joinEmail.getText().toString();
                            userModel.userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            String uid = task.getResult().getUser().getUid();
                            mDatabase.getReference().child("Users").child(uid).setValue(userModel);

                            // 회원가입 완료후 JoinActivity 종료하기
                            finish();

                        } else {    // 회원가입 실패
                            Log.w("파베 회원가입", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(JoinActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("회원가입액티비티", "=====> onStart 호출됨");

        // 회원가입 나간지 5초지나면 텍스트창 비우기
        timeOut = 0;
        if (currentTime != 0) {
            timeOut = System.currentTimeMillis() - currentTime;
            if (timeOut > 5000) {

                edit_joinName.getText().clear();
                edit_joinEmail.getText().clear();
                edit_joinPassword.getText().clear();
                edit_joinPasswordCk.getText().clear();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("회원가입액티비티", "=====> onResume 호출됨");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("회원가입액티비티", "=====> onPause 호출됨");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("회원가입액티비티", "=====> onStop 호출됨");

        // onStop되었을때의 시간을 가져온다.
        currentTime = System.currentTimeMillis();

//        finish();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("회원가입액티비티", "=====> onRestart 호출됨");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("회원가입액티비티", "=====> onDestroy 호출됨");

    }

}
