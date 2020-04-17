package com.example.mydiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mydiary.Adapter.MyDiaryAdapter;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.model.DiaryModel;
import com.example.mydiary.sp.SharedMethod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.mydiary.sp.SharedMethod.getSPJA;

public class DiaryCollectionActivity extends AppCompatActivity {

    public static ArrayList<Item_myDiary> Diary_items = new ArrayList<>();

    ImageButton btn_closeCollection;
    TextView tv_choiceType, tv_choiceMONTH, tv_choiceTAG, tv_choiceALL;
    LinearLayout ll_choiceTAG;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    String cYear, cMonth, cMonth1,yearMonth;

    String pMonth1, pYearMonth;


    Calendar calendar;
    int current_year, current_month;
    String current_month1, currentYearMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_collection);
        Diary_items = getSPJA(this);

        ll_choiceTAG = findViewById(R.id.ll_choiceTAG);

        btn_closeCollection = (ImageButton) findViewById(R.id.btn_closeCollection);
        tv_choiceType = (TextView) findViewById(R.id.tv_choiceType);
        tv_choiceMONTH = (TextView) findViewById(R.id.tv_choiceMONTH);
        tv_choiceMONTH.setPaintFlags(tv_choiceMONTH.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);   // 밑줄긋기

        tv_choiceTAG = (TextView) findViewById(R.id.tv_choiceTAG);
        tv_choiceALL = (TextView) findViewById(R.id.tv_choiceALL);

        // 현재 년, 달 가져오기
        calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH) + 1;


        if (current_month < 10) {    // 월이 10보다 작으면 앞에 0을 붙힌다.
            current_month1 = "0" + String.valueOf(current_month);
            currentYearMonth = current_year + current_month1;                      // 현재 년,월   ex) 201905
            tv_choiceType.setText(current_year + ". " + current_month1 + " ▾");
        } else {
            currentYearMonth = String.valueOf(current_year) + String.valueOf(current_month);
            tv_choiceType.setText(current_year + ". " + current_month + " ▾");
        }


        final DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
                if (monthOfYear < 10) {
                    pMonth1 = "0" + String.valueOf(monthOfYear);
                    pYearMonth = year + pMonth1;
                    tv_choiceType.setText(year + ". " + pMonth1 + " ▾");

                } else {
                    pYearMonth = String.valueOf(year + monthOfYear);
                    tv_choiceType.setText(year + ". " + monthOfYear + " ▾");
                }


                // 현재 년월, 또는 선택한 년월에 맞는 데이터 나오도록 하는 코드
                ArrayList<Item_myDiary> item_myDiaries = new ArrayList<>();
                item_myDiaries.clear();
                for (Item_myDiary item_myDiary : Diary_items) {
                    String[] strings = item_myDiary.getDiaryDate().split(". ");
                    cYear = strings[0];
                    cMonth = strings[1];
                    if (cMonth.equals("1") ||cMonth.equals("2") ||cMonth.equals("3") ||cMonth.equals("4") ||cMonth.equals("5") ||cMonth.equals("6") ||cMonth.equals("7") ||cMonth.equals("8") ||cMonth.equals("9")) {
                        cMonth1 = "0" + cMonth;
                        yearMonth = cYear + cMonth1;
                    }else{
                        yearMonth = cYear + cMonth;
                    }
                    if (yearMonth.equals(pYearMonth)) {
                        item_myDiaries.add(item_myDiary);
                    }
                }
                mAdapter = new MyDiaryAdapter(item_myDiaries, DiaryCollectionActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            }
        };
        // 현재 년월, 또는 선택한 년월에 맞는 데이터 나오도록 하는 코드
        ArrayList<Item_myDiary> item_myDiaries = new ArrayList<>();

        item_myDiaries.clear();
        for (Item_myDiary item_myDiary : Diary_items) {

            String[] strings = item_myDiary.getDiaryDate().split(". ");
            cYear = strings[0];
            cMonth = strings[1];
            Log.d("month",cMonth);
            if (cMonth.equals("1") ||cMonth.equals("2") ||cMonth.equals("3") ||cMonth.equals("4") ||cMonth.equals("5") ||cMonth.equals("6") ||cMonth.equals("7") ||cMonth.equals("8") ||cMonth.equals("9")) {
                cMonth1 = "0" + cMonth;
                yearMonth = cYear + cMonth1;
            }else{
                yearMonth = cYear + cMonth;
            }
            Log.d("yearMonth", yearMonth);
            if (yearMonth.equals(currentYearMonth)) {
                item_myDiaries.add(item_myDiary);
                Log.d("yeatMonthArray", String.valueOf(item_myDiaries));
            }
        }

        // 리싸이클러뷰 적용
        mRecyclerView = findViewById(R.id.recyclerView_diaryCollection);    // 리싸이클러뷰 id 가져오기
        mRecyclerView.setHasFixedSize(true);    // 아이템들이 보여지는것을 일정하게 한다.

//        mAdapter = new MyDiaryAdapter(Diary_items, this);
        mAdapter = new MyDiaryAdapter(item_myDiaries, this);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);     // 앞에 선언한 리싸이클러뷰를 레이아웃매니저에 붙힌다.



        tv_choiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });

        // 왼쪽상단에 뒤로 가기 버튼 누르면 일기모아보기 화면 종료하기
        btn_closeCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 오른쪽상단에 MONTH, TAG, ALL 종류별로 다른 텍스트 뜨게 하기
        // 1. MONTH 클릭했을 때 ( 년, 월을 선택할 수 있고, 선택한 달의 일기 보이기 )
        tv_choiceMONTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 년, 달 가져오기
                calendar = Calendar.getInstance();
                current_year = calendar.get(Calendar.YEAR);
                current_month = calendar.get(Calendar.MONTH) + 1;

                if (current_month < 10) {    // 월이 10보다 작으면 앞에 0을 붙힌다.
                    current_month1 = "0" + String.valueOf(current_month);
                }
                tv_choiceType.setText(current_year + ". " + current_month1 + " ▾");

                // 가운데 선택하면 보고싶은 달 선택할 수 있게
                tv_choiceType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                        pd.setListener(d);
                        pd.show(getSupportFragmentManager(), "YearMonthPickerTest");


                    }
                });




                // 현재 년월, 또는 선택한 년월에 맞는 데이터 나오도록 하는 코드
                ArrayList<Item_myDiary> item_myDiaries = new ArrayList<>();

                item_myDiaries.clear();
                for (Item_myDiary item_myDiary : Diary_items) {

                    String[] strings = item_myDiary.getDiaryDate().split(". ");
                    cYear = strings[0];
                    cMonth = strings[1];
                    if (cMonth.equals("1") ||cMonth.equals("2") ||cMonth.equals("3") ||cMonth.equals("4") ||cMonth.equals("5") ||cMonth.equals("6") ||cMonth.equals("7") ||cMonth.equals("8") ||cMonth.equals("9")) {
                        cMonth1 = "0" + cMonth;
                        yearMonth = cYear + cMonth1;
                    }else{
                        yearMonth = cYear + cMonth;
                    }
                    if (yearMonth.equals(currentYearMonth)) {
                        item_myDiaries.add(item_myDiary);
                    }
                }
                mAdapter = new MyDiaryAdapter(item_myDiaries, v.getContext());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


                tv_choiceMONTH.setPaintFlags(tv_choiceMONTH.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);   // 밑줄긋기
                tv_choiceTAG.setPaintFlags(tv_choiceTAG.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제
                tv_choiceALL.setPaintFlags(tv_choiceALL.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제

                tv_choiceMONTH.setTextColor(Color.parseColor("#6E6E6E"));   // 색 진하게
                // 다른애들은 연하게
                // 선택한애들이 진해지고 나머지애들은 원래대로 연하게 밑줄없는 상태로 돌리고 싶다. 방법을 찾지 못해서 일단 밑줄은 나중에 하는걸로. -> 찾아서 해결했지롱
                tv_choiceTAG.setTextColor(Color.parseColor("#D8D8D8"));
                tv_choiceALL.setTextColor(Color.parseColor("#D8D8D8"));

            }
        });

    } //onCreate()


    void sort(){
        Collections.sort(Diary_items, new Comparator<Item_myDiary>() {
            @Override
            public int compare(Item_myDiary o1, Item_myDiary o2) {
                String date1 = o1.getDiaryDate();
                String date2 = o2.getDiaryDate();

                int sComp = date1.compareTo(date2);

                return sComp;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    } //onStart()

    @Override
    protected void onResume() {
        super.onResume();


        // 2. TAG 클릭했을 때 ( TAG 색을 선택할 수 있고, 선택한 색 별로 보이기 )
        tv_choiceTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_choiceType.setText("TAG ▾"); // 상단가운데 textview에 TAG 표시
                tv_choiceTAG.setPaintFlags(tv_choiceTAG.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);   // 밑줄긋기
                tv_choiceMONTH.setPaintFlags(tv_choiceMONTH.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제
                tv_choiceALL.setPaintFlags(tv_choiceALL.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제

                tv_choiceTAG.setTextColor(Color.parseColor("#6E6E6E")); // 색 진하게
                // 다른애들은 연하게
                tv_choiceMONTH.setTextColor(Color.parseColor("#D8D8D8"));
                tv_choiceALL.setTextColor(Color.parseColor("#D8D8D8"));

                tv_choiceType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*AlertDialog.Builder dig_Tag = new AlertDialog.Builder(DiaryCollectionActivity.this);
                        dig_Tag.setTitle("아?").setPositiveButton("아", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog Dialog_Tag = dig_Tag.create();
                        Dialog_Tag.show();*/

                        if (ll_choiceTAG.getVisibility() == View.GONE) {
                            ll_choiceTAG.setVisibility(View.VISIBLE);
                        } else {
                            ll_choiceTAG.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });

        // 3. ALL 클릭했을 때 ( 일기 전체 다 보여주기 )
        tv_choiceALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapter = new MyDiaryAdapter(Diary_items, v.getContext());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                tv_choiceType.setText("ALL");
                tv_choiceALL.setPaintFlags(tv_choiceALL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);   // 밑줄긋기
                tv_choiceMONTH.setPaintFlags(tv_choiceMONTH.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제
                tv_choiceTAG.setPaintFlags(tv_choiceTAG.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));   // 밑줄해제

                tv_choiceALL.setTextColor(Color.parseColor("#6E6E6E")); // 색 진하게
                // 다른애들은 연하게
                tv_choiceMONTH.setTextColor(Color.parseColor("#D8D8D8"));
                tv_choiceTAG.setTextColor(Color.parseColor("#D8D8D8"));

                tv_choiceType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

            }
        });

    } //onResume()


}
