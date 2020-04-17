package com.example.mydiary;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mydiary.sp.SharedMethod;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {

    ImageView iv_diaryNotification, iv_foldCalendar_up, iv_foldCalendar_down, iv_diary;
    TextView tv_diary, tv_diaryNotification;
    LinearLayout ll_diaryNotification, ll_dairy, btn_othersDiary, btn_search, btn_diaryCollection, btn_more;
    CalendarView mCalendarView;
    int select_year, select_month, select_dayOfMonth, dayOfWeek;
    Calendar calendar;

    ChangeSharedItem changeSharedItem;

    Handler mHandler;
    long minn;

    Animation mCalendarMoveUp, mCalendarMoveDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        iv_diaryNotification = (ImageView) findViewById(R.id.iv_diaryNotification);         // 글 쓸 시간을 정하는 버튼
        ll_diaryNotification = (LinearLayout) findViewById(R.id.ll_diaryNotification);      // 글쓰기 00시간 00분전
        tv_diaryNotification = (TextView) findViewById(R.id.tv_diaryNotification);          // 00시간 00 분전

        iv_foldCalendar_up = (ImageView) findViewById(R.id.iv_foldCalendar_up);         // 달력 접는 버튼
        iv_foldCalendar_down = (ImageView) findViewById(R.id.iv_foldCalendar_down);     // 달력 펼치는 버튼

        ll_dairy = (LinearLayout) findViewById(R.id.ll_dairy);
        iv_diary = (ImageView) findViewById(R.id.iv_diary);
        tv_diary = (TextView) findViewById(R.id.tv_diary);

        btn_othersDiary = (LinearLayout) findViewById(R.id.btn_othersDiary);
        btn_search = (LinearLayout) findViewById(R.id.btn_search);
        btn_diaryCollection = (LinearLayout) findViewById(R.id.btn_diaryCollection);
        btn_more = (LinearLayout) findViewById(R.id.btn_more);

        mCalendarMoveUp = AnimationUtils.loadAnimation(this,R.anim.riseup);     // 위로 올라가는 애니메이션
        mCalendarMoveDown = AnimationUtils.loadAnimation(this,R.anim.dropdown);   // 아래로 내려가는 애니메이션

        /*// 나중에 삭제할것 -------------
        AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);
        dig.setTitle("작성중이던 글이 있습니다.").setMessage("이어서 작성할까요?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent_write = new Intent(MainActivity.this, WriteActivity.class);
                        startActivity(intent_write);
                    }
                })
                .setNegativeButton("아니요. 지워주세요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = dig.create();
        dialog.show();
        // ------------------------------*/

        // 현재날짜 구하기
        calendar = Calendar.getInstance();
        select_year = calendar.get(Calendar.YEAR);                  // 년
        int month = calendar.get(Calendar.MONTH);                   // 월
        select_month = month + 1;
        select_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 요일구하기
        String engDayOfWeek = "";
        switch (dayOfWeek) {
            case 1:
                engDayOfWeek = "SUN";
                break;
            case 2:
                engDayOfWeek = "MON";
                break;
            case 3:
                engDayOfWeek = "TUE";
                break;
            case 4:
                engDayOfWeek = "WEN";
                break;
            case 5:
                engDayOfWeek = "THE";
                break;
            case 6:
                engDayOfWeek = "FIR";
                break;
            case 7:
                engDayOfWeek = "SAT";
                break;
        }



        // 글 쓸 시간을 정하는 아이콘
        // 누른다 -> 글 쓸 시간을 정할 수 있다고 알려준다. -> 예 누르면 글쓸 시간을 정하고 시간 타이머가 나온다.
        iv_diaryNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dig_setTime = new AlertDialog.Builder(MainActivity.this);
                dig_setTime.setTitle("글 쓸 시간을 정할 수 있습니다.")
                        .setMessage("시간을 정하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TimePickerDialog dig_time = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                        String msg = String.format("%d시 %d분", hour, min);

//                                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        iv_diaryNotification.setVisibility(View.GONE);      // 기존에 있던 종모양 아이콘을 사라지게 하고
                                        ll_diaryNotification.setVisibility(View.VISIBLE);   // 설정한 시간까지의 타이머를 보이게 한다.

                                        // 현재시간과 설정한 시간의 차이를 구한다.
                                        String start = "2019-05-01 " + hour + ":" + min + ":00";
                                        Calendar tempcal = Calendar.getInstance();
                                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date startday = sf.parse(start, new ParsePosition(0));
                                        long startTime = startday.getTime();

                                        Calendar cal = Calendar.getInstance();
                                        Date endDate = cal.getTime();
                                        long endTime = endDate.getTime();
//                                        long mills = endTime - startTime;             // 원래는 이게 맞다.
                                        long mills = startTime - endTime;

                                        long millss = mills % (3600 * 24 * 1000);
                                        minn = millss / 60000;

                                        StringBuffer diffTime = new StringBuffer();
                                        diffTime.append("시간의 차이는").append(minn).append("분 입니다.");
//                                        tv_diaryNotification.setText(diffTime.append("글쓰기").append( minn).append("분 전입니다."));
                                        tv_diaryNotification.setText(minn + "분 전입니다.");
                                        ShowTimeMethod();

                                        System.out.println(diffTime.toString());

                                        Log.d("시간차이", diffTime.toString());


                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지
                                dig_time.show();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog_setTime = dig_setTime.create();
                dialog_setTime.show();

            }
        });

        // 달력 접어올리는 버튼
        iv_foldCalendar_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCalendarView.setVisibility(View.GONE);
                iv_foldCalendar_up.setVisibility(View.INVISIBLE);
                iv_foldCalendar_down.setVisibility(View.VISIBLE);

//                mCalendarView.startAnimation(mCalendarMoveUp);
            }
        });

        // 달력 펼치는 버튼
        iv_foldCalendar_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_foldCalendar_up.setVisibility(View.VISIBLE);
                mCalendarView.setVisibility(View.VISIBLE);
                iv_foldCalendar_down.setVisibility(View.GONE);

                mCalendarView.startAnimation(mCalendarMoveDown);
            }
        });


        // 캘린더에 날짜를 선택하는 클릭리스너
        // 여기서 날짜를 선택하면 하단 textview에 일기 내용이 나온다.
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                select_year = year;
                select_month = month + 1;
                select_dayOfMonth = dayOfMonth;
                String date = year + ". " + (month + 1) + ". " + dayOfMonth;

                // 날짜를 선택하면 해당 날짜에 저장된 데이터가 아래에 나오도록
                changeSharedItem = SharedMethod.selectDate2item(MainActivity.this, date);
                if (changeSharedItem != null) {
                    if (!changeSharedItem.item_myDiary.diaryPicture.equals("")) {
                        iv_diary.setVisibility(View.VISIBLE);
                        byte[] decodedByteArray = Base64.decode(changeSharedItem.item_myDiary.getDiaryPicture(), Base64.NO_WRAP);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                        iv_diary.setImageBitmap(decodedBitmap);

                    } else {
                        iv_diary.setVisibility(View.GONE);
                    }

                    tv_diary.setText(changeSharedItem.item_myDiary.getDiaryText());

                } else {
                    iv_diary.setVisibility(View.GONE);
                    tv_diary.setText("작성된 글이 없습니다.");
                }

            }
        });

        // 일기 내용이 보여질 TextView를 선택하여 글 작성 페이지로 넘어가기
        ll_dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_write = new Intent(MainActivity.this, WriteActivity.class);

                if (changeSharedItem != null) {
                    intent_write.putExtra("position", changeSharedItem.n);
                } else {
                    intent_write.putExtra("년", select_year);
                    intent_write.putExtra("월", select_month);
                    intent_write.putExtra("일", select_dayOfMonth);
                }
                startActivity(intent_write);
            }
        });

        // 하단 첫번째 버튼은 다른사람들이 공개한 그들의 하루일기를 모아 볼 수 있는 페이지 이다.
        btn_othersDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_othersDiary = new Intent(MainActivity.this, PublicDiaryActivity.class);
                startActivity(intent_othersDiary);
            }
        });

        // 하단 두번째 버튼은 내가 쓴 일기들을 단어로 검색해서 볼 수 있는 페이지이다.
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_searchDiary = new Intent(MainActivity.this, SearchDiaryActivity.class);
                startActivity(intent_searchDiary);
            }
        });

        // 하단 세번째 버튼은 홈화면. 즉 현재페이지이기 때문에 온클릭 줄 필요 없음

        // 하단 네번째 버튼은 내가 쓴 일기들을 모아보는 페이지이다.
        btn_diaryCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_diaryCollection = new Intent(MainActivity.this, DiaryCollectionActivity.class);
                startActivity(intent_diaryCollection);
            }
        });

        // 하단 다섯번째 버튼은 설정이나 로그아웃등을 할 수 있는 더보기 페이지이다.
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_more = new Intent(MainActivity.this, MoreActivity.class);
                startActivity(intent_more);
            }
        });


    } //onCreate()

    // 핸들러
    public void ShowTimeMethod() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                minn--;
                tv_diaryNotification.setText(minn + "분 전입니다.");
            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                    }
                    mHandler.sendEmptyMessage(1);    //  핸들러를 호출한다. 즉, 시간을 최신화 해준다.
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 오늘날짜의 달력 보여주는애
        String date1 = select_year + ". " + (select_month) + ". " + select_dayOfMonth;
        changeSharedItem = SharedMethod.selectDate2item(MainActivity.this, date1);
        if (changeSharedItem != null) {
            if (!changeSharedItem.item_myDiary.diaryPicture.equals("")) {
                iv_diary.setVisibility(View.VISIBLE);
                byte[] decodedByteArray = Base64.decode(changeSharedItem.item_myDiary.getDiaryPicture(), Base64.NO_WRAP);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                iv_diary.setImageBitmap(decodedBitmap);

            } else {
                iv_diary.setVisibility(View.GONE);
            }

            tv_diary.setText(changeSharedItem.item_myDiary.getDiaryText());
        }

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
