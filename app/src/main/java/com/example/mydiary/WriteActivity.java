package com.example.mydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.model.DiaryModel;
import com.example.mydiary.sp.SharedMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {

    ImageButton btn_writeCancel, btn_writeSave, btn_time, btn_choicePicture;
    ImageView iv_choicePicture, btn_choiceTag;
    EditText et_writeDiary;
    TextView tv_choiceDate, tv_time, tv_countText;
    LinearLayout ll_choiceTAG;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;

    Calendar time;
    SimpleDateFormat sdf;

    String current_time, engDayOfWeek;
    int dayOfWeek;

    Bitmap img = null;

    Uri diary_image;
    String imageUrl_string = "";
    int check_change = -1;

    int select_year, select_month, select_dayOfMonth;

    String pic = "";

    // 현재시간을 보여주는 메서드
    public void ShowTimeMethod() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//              Show_Time_TextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                tv_time.setText(sdf.format(new Date()));
            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    handler.sendEmptyMessage(1);    //  핸들러를 호출한다. 즉, 시간을 최신화 해준다.
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();

    }   //ShowTimeMethod()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Log.d("글쓰기화면 =====>", "onCreate 실행");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        AndroidBug5497Workaround.assistActivity(this);

        Intent intent_select = getIntent();                                     // MainActivity 에서 선택한 날짜를 intent 로 가져온다.
        select_year = intent_select.getIntExtra("년", 0);
        select_month = intent_select.getIntExtra("월", 0);
        select_dayOfMonth = intent_select.getIntExtra("일", 0);

        btn_writeCancel = (ImageButton) findViewById(R.id.btn_writeCancel);     // 글쓰기 취소
        tv_choiceDate = (TextView) findViewById(R.id.tv_choiceDate);            // 날짜 선택
        tv_choiceDate.setText(select_year + ". " + select_month + ". " + select_dayOfMonth);    // 선택한날짜가 화면 상단에 찍힌다.
        btn_writeSave = (ImageButton) findViewById(R.id.btn_writeSave);         // 쓴 글 저장
        iv_choicePicture = (ImageView) findViewById(R.id.iv_choicePicture);     // 선택한 사진 보이는 ImageView
        et_writeDiary = (EditText) findViewById(R.id.et_writeDiary);            // 일기 입력되는 곳
        btn_time = (ImageButton) findViewById(R.id.btn_time);                   // 시계 아이콘을 누르면 현재 시간,분 이 텍스트로 입력된다.
        tv_time = (TextView) findViewById(R.id.tv_time);                        // 시계 아이콘 옆에 현재시간이 나타난다.
        ShowTimeMethod();
        btn_choicePicture = (ImageButton) findViewById(R.id.btn_choicePicture); // 카메라 아이콘을 누르면 사진을 업로드 할 수 있다.
        btn_choiceTag = (ImageView) findViewById(R.id.btn_choiceTag);            // TAG 글씨를 눌러 현재 글의 TAG 색을 지정 할 수 있다.
        ll_choiceTAG = (LinearLayout) findViewById(R.id.ll_choiceTAG);
        tv_countText = (TextView) findViewById(R.id.tv_countText);              // 입력하는 수대로 수가 증가한다.


        // 글을 새로 작성하는게 아니라 수정할때 전에 입력되있던 값들을 가져오는 코드
        Intent intent = getIntent();
        check_change = intent.getIntExtra("position", -1);

        if (check_change != -1) {
            Log.d("몇번이게", "" + check_change);
            DiaryCollectionActivity.Diary_items = SharedMethod.getSPJA(this);
            Item_myDiary myDiary = DiaryCollectionActivity.Diary_items.get(check_change);

            if (!myDiary.diaryPicture.equals("")) {
                byte[] decodedByteArray = Base64.decode(myDiary.diaryPicture, Base64.NO_WRAP);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                iv_choicePicture.setImageBitmap(decodedBitmap);
                iv_choicePicture.setVisibility(View.VISIBLE);
            }
            et_writeDiary.setText(myDiary.getDiaryText());
            tv_choiceDate.setText(myDiary.getDiaryDate());
        }

        // 상단에 취소, 저장버튼
        // 일기 작성 취소버튼
        btn_writeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 취소 버튼을 눌렀을때 입력한 글이 없으면 그냥 끄고 입력한 글이 있으면 작성중이던 글이 있다고 다이얼로그 띄우기
                if (et_writeDiary.getText().toString().length() == 0) {
                    finish();
                } else {
                    AlertDialog.Builder dig_writeCancel = new AlertDialog.Builder(v.getContext());
                    dig_writeCancel.setTitle("작성중이던 글이 있습니다.").setMessage("정말 취소하시겠습니까?")
                            .setPositiveButton("넹", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNegativeButton("아니용", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog_writeCancel = dig_writeCancel.create();
                    dialog_writeCancel.show();
                }
            }
        });


        // 일기 저장버튼
        btn_writeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nowUID = mAuth.getCurrentUser().getUid();    // 로그인한 현재 유저의 UID

                // 입력된게 아무것도 없으면 저장 못행
                if (img == null && et_writeDiary.getText().toString().length() == 0) {

                    AlertDialog.Builder diary_null = new AlertDialog.Builder(WriteActivity.this);
                    diary_null.setTitle("작성하신 글이 없습니다.")
                            .setPositiveButton("네 ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = diary_null.create();
                    dialog.show();

                } else {    // 사진이나, 텍스트 있으면 저장 가능

                    // DB에 저장할목록들
                    final DiaryModel diaryModel = new DiaryModel();

                    // 쪼개서 배열에 넣고 합치기
                    String[] date = tv_choiceDate.getText().toString().split(". ");
                    String year = date[0];
                    String month = date[1];
                    String day = date[2];

                    if (Integer.parseInt(month) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
                        month = "0" + month;
                    }
                    if (Integer.parseInt(day) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
                        day = "0" + day;
                    }
                    final String saveDate = year + month + day;


                    if (img != null) {  // 이미지가 있을때
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(imageBytes, Base64.NO_WRAP);


                        mStorageRef = FirebaseStorage.getInstance().getReference().child("storyImage").child(saveDate).child(nowUID);
                        mStorageRef.putFile(diary_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                imageUrl_string = task.getResult().getDownloadUrl().toString();
                                Log.d("이미지 유알엘 스트링형식", imageUrl_string);

                                // DB에 저장하기
                                diaryModel.diaryDate = tv_choiceDate.getText().toString();
                                diaryModel.diaryText = et_writeDiary.getText().toString();
                                diaryModel.diaryPicture = imageUrl_string;
                                diaryModel.userUID = nowUID;
                                if (check_change == -1) {    // 일기를 처음 입력할때

                                    saveSharedPreferences();

                                    mDatabase.getReference().child("Diarys").child(nowUID).child(saveDate).setValue(diaryModel);


                                } else {    // 일기를 수정할때

                                    changeSharedPreferences(check_change);

                                    mDatabase.getReference().child("Diarys").child(nowUID).child(saveDate).setValue(diaryModel);

                                }


                            }
                        });


                    } else {    // 이미지가 없을때

                        // DB에 저장하기
                        diaryModel.diaryDate = tv_choiceDate.getText().toString();
                        diaryModel.diaryText = et_writeDiary.getText().toString();
                        diaryModel.diaryPicture = imageUrl_string;
                        if (check_change == -1) {    // 일기를 처음 입력할때
                            saveSharedPreferences();

                            mDatabase.getReference().child("Diarys").child(nowUID).child(saveDate).setValue(diaryModel);


                        } else {    // 일기를 수정할때
                            changeSharedPreferences(check_change);

                            mDatabase.getReference().child("Diarys").child(nowUID).child(saveDate).setValue(diaryModel);

                        }

                    }

                    finish();
                }
            }


        });

        // editText에 입력하는 글자의 개수를 센다.
        et_writeDiary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_countText.setText(Integer.toString(s.toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 하단 버튼들 누르기
        // 하단에 시계아이콘 누르면 현재시간이 글 작성하는 부분에 찍힌다.
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = Calendar.getInstance();
                sdf = new SimpleDateFormat("hh:mm a", new Locale("en", "US"));

                current_time = sdf.format(time.getTime());

                et_writeDiary.setText(et_writeDiary.getText().toString() + current_time);
                et_writeDiary.setSelection(et_writeDiary.getText().length());   // 입력된 글 맨뒤로 커서 위치시키기

            }
        });

        // 하단에 카메라 아이콘을 누르면 갤러리도 이동하여 업로드한 사진을 고를 수 있다.
        btn_choicePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_gallery = new Intent(Intent.ACTION_PICK);
                intent_gallery.setType("image/*");
                intent_gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent_gallery, 1);

            }
        });

        // 하단에 TAG 글씨를 누르면 이 글의 TAG 색상을 지정할 수 있다.
        btn_choiceTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ll_choiceTAG.getVisibility() == View.GONE) {
                    ll_choiceTAG.setVisibility(View.VISIBLE);
                } else {
                    ll_choiceTAG.setVisibility(View.GONE);
                }


            }
        });

    } //onCreate()

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("글쓰기화면 =====>", "onStart 실행");

/*        // 작성중이던 글이 있는지 없는지 확인하고 있으면 다이얼로그 창 띄우기                  -> onStart에 쓰니까 사진 올리러 갤러리 갔다와도 체크를 해서 이 다이얼로그가 뜬다.
                                                                                             처음 글쓰기 화면에만 떠야하니까 여기에 쓰면 안될것같다.
        if (et_writeDiary.getText().toString().length() != 0) {

            AlertDialog.Builder dig = new AlertDialog.Builder(WriteActivity.this);
            dig.setTitle("작성중이던 글이 있습니다.").setMessage("불러올까요?")
                    .setPositiveButton("네 ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("아니요 지워주세여", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            et_writeDiary.setText(null);
                        }
                    });
            AlertDialog dialog = dig.create();
            dialog.show();
        }*/

    } //onStart()

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("글쓰기화면 =====>", "onResume 실행");

    } //onResume()

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("글쓰기화면 =====>", "onPause 실행");

    } //onPause()

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("글쓰기화면 =====>", "onStop 실행");


    } //onStop()

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("글쓰기화면 =====>", "onRestart 실행");

    } //onRestart()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("글쓰기화면 =====>", "onDestroy 실행");

    } //onDestroy()

    // 갤러리에서 사진 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to // 어떤 요청에 응답하는지 확인
        if (requestCode == 1) {
            // Make sure the request was successful // 요청이 성공했는지 확인
            if (resultCode == RESULT_OK) {
                try {
                    Log.d("onActivityResult", data.getDataString());
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    iv_choicePicture.setVisibility(View.VISIBLE);
                    iv_choicePicture.setImageBitmap(img);
                    iv_choicePicture.setImageURI(data.getData());
                    diary_image = data.getData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void saveSharedPreferences() {
        //==================================================================================
        // SharedPreferences에 저장하기
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences
        // diary.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
        SharedPreferences diary = getSharedPreferences("diary", MODE_PRIVATE);
        // editor
        SharedPreferences.Editor diary_editor = diary.edit();
        // JSONArray
        // key : 로그인된 이메일 , value : JSONObject
        String getJSONArray = diary.getString(user_email, null);
        JSONArray jsonArray = new JSONArray();
        if (getJSONArray != null) {
            try {
                jsonArray = new JSONArray(getJSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // JSONObject
        // key : 날짜, 사진, 내용 , value :
        JSONObject obj = new JSONObject();
        try {
            obj.put("날짜", tv_choiceDate.getText().toString());
            obj.put("사진", pic);
            obj.put("내용", et_writeDiary.getText().toString());

            jsonArray.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        diary_editor.putString(user_email, String.valueOf(jsonArray));
        diary_editor.commit();
    }

    protected void changeSharedPreferences(int position) {
        //==================================================================================
        // SharedPreferences에 저장하기
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences
        // diary.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
        SharedPreferences diary = getSharedPreferences("diary", MODE_PRIVATE);
        // editor
        SharedPreferences.Editor diary_editor = diary.edit();
        // JSONArray
        // key : 로그인된 이메일 , value : JSONObject
        String getJSONArray = diary.getString(user_email, null);
        JSONArray jsonArray = new JSONArray();
        if (getJSONArray != null) {
            try {
                jsonArray = new JSONArray(getJSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // JSONObject
        // key : 날짜, 사진, 내용 , value :
        JSONObject obj = new JSONObject();
        try {
            obj.put("날짜", tv_choiceDate.getText().toString());
            obj.put("사진", pic);
            obj.put("내용", et_writeDiary.getText().toString());

            jsonArray.put(position, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        diary_editor.putString(user_email, String.valueOf(jsonArray));
        DiaryCollectionActivity.Diary_items.set(position, new Item_myDiary(pic, tv_choiceDate.getText().toString(), et_writeDiary.getText().toString()));
        diary_editor.commit();
    }


}