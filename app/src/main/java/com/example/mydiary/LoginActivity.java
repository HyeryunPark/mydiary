package com.example.mydiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    TextView tv_join;
    EditText login_editEmail, login_editPassword;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("로그인액티비티", "=====> onCreate 호출됨");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        login_editEmail = (EditText) findViewById(R.id.login_editEmail);            // 로그인 이메일 입력창
        login_editPassword = (EditText) findViewById(R.id.login_editPassword);      // 로그인 비밀번호 입력창
        btn_login = (Button) findViewById(R.id.btn_login);                          // 로그인 버튼
        tv_join = (TextView) findViewById(R.id.tv_join);                            // 회원가입 textview
        tv_join.setPaintFlags(tv_join.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // ==================================================================================================================
        // 자동로그인
        // setting.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동 생성한다.)
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        // key값 : autoLogin, value값 : 아이디, 비밀번호 (value값에 아이디,비밀번호등 중요한 정보를 담는것은 위험)
        String AutoLogin = setting.getString("autoLogin", "");

        // 이메일입력창이 비어있지 않으면
        if (AutoLogin.length() != 0) {
            // 자동로그인되었다는 토스트메세지를 띄우고 홈화면으로 넘어가기
            Toast.makeText(LoginActivity.this, "자동로그인되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent_home = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent_home);
            finish();

            // 이메일입력창이 비어있으면
        } else {
            // 로그인 버튼 누르기
            // 빈칸 있는지 체크하기 - ok
            // 회원가입 되어있는 아이디, 비밀번호인지 확인하기 - ok
            // 로그인과 동시에 자동로그인 되기 - ok
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (login_editEmail.getText().toString().length() != 0 && login_editPassword.getText().toString().length() != 0) {

                        // 회원가입 정보 가져오기
                        // join.xml이라는 이름의 SharedPreferences 파일을 가져온다.
                        SharedPreferences join = getSharedPreferences("join", MODE_PRIVATE);

                        // 입력한 이메일이 저장된 정보에 존재하지않을때
                        if (join.getString(login_editEmail.getText().toString(), "").equals("")) {
                            Toast.makeText(LoginActivity.this, "존재하지 않는 계정입니다." + join.getString(login_editEmail.getText().toString(), ""), Toast.LENGTH_SHORT).show();

                        } else {    // 입력한 이메일이 저장된 정보에 존재할때 => 입력한 비밀번호와 저장된 비밀번호가 일치하는지 확인
                            // 키값 가져오기
                            String email = join.getString(login_editEmail.getText().toString(), "");
                            // 문자열 쪼개서 배열에 넣기
                            String[] user_info = email.split(",");
                            String user_name = user_info[0];
                            String user_password = user_info[1];

                            // 입력한 비밀번호와 저장된 비밀번호가 일치한다. 로그인 성공
                            // => 자동 로그인 저장
                            if (login_editPassword.getText().toString().equals(user_password)) {

                                SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = setting.edit();
                                autoLogin.putString("autoLogin", login_editEmail.getText().toString() + "," + login_editPassword.getText().toString());
                                autoLogin.commit();

                                Toast.makeText(LoginActivity.this , "로그인성공. 환영합니다.",Toast.LENGTH_SHORT).show();

                                loginEvent();

                                Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent_login);
                                finish();

                            } else {    // 입력한 비밀번호와 저장된 비밀번호가 일치하지 않는다. 로그인실패
                                Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }   // 자동로그인
        // ==================================================================================================================

        // 로그인 인터페이스 리스너 ( 로그인이 됐는지 확인해주는 )
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // 로그인

                } else {
                    // 로그아웃

                }
            }
        };



        // 회원가입하러가기 누르기
        tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_join = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent_join);
            }
        });


    } // onCreate()

    // 로그인이 완료되면 정상적으로 로그인이 성공했는지 실패했는지 판단해주는곳
    // 로그인이 되고 다음 화면으로 넘어가는 애 아님
    void loginEvent(){
        mAuth.signInWithEmailAndPassword(login_editEmail.getText().toString(), login_editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("파베 로그인", "signInWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("파베 로그인", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("로그인액티비티", "=====> onStart 호출됨");

        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("로그인액티비티", "=====> onResume 호출됨");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("로그인액티비티", "=====> onPause 호출됨");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("로그인액티비티", "=====> onStop 호출됨");

        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("로그인액티비티", "=====> onRestart 호출됨");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("로그인액티비티", "=====> onDestroy 호출됨");

    }
}
