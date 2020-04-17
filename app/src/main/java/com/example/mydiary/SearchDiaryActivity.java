package com.example.mydiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mydiary.Adapter.SearchDiaryAdapter;
import com.example.mydiary.Item.Item_myDiary;

import java.util.ArrayList;

import static com.example.mydiary.sp.SharedMethod.getSPJA;


public class SearchDiaryActivity extends AppCompatActivity {

    public static ArrayList<Item_myDiary> filteredList = new ArrayList<>();
    
    ImageButton btn_closeSearch, btn_searchDiary;
    EditText et_searchDiary;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SearchDiaryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_diary);

        btn_closeSearch = (ImageButton)findViewById(R.id.btn_closeSearch);  // 현재페이지를 종료하는 버튼
        et_searchDiary = (EditText)findViewById(R.id.et_searchDiary);       // 검색어를 입력하는곳
        btn_searchDiary = (ImageButton)findViewById(R.id.btn_searchDiary);  // 검색하기 버튼

        // 검색화면 종료하기
        btn_closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 검색어 입력하기
        et_searchDiary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        // 검색어를 입력하고 버튼 누르기
        btn_searchDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 리싸이클러뷰 적용하기
        mRecyclerView = findViewById(R.id.recyclerView_searchDiary);    // 리싸이클러뷰 id 가져오기
        mRecyclerView.setHasFixedSize(true);    // 아이템들이 보여지는것을 일정하게 한다.

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);     // 앞에 선언한 리싸이클러뷰를 레이아웃매니저에 붙힌다.

        mAdapter = new SearchDiaryAdapter(filteredList,this);
        mRecyclerView.setAdapter(mAdapter);


    } //onCreate()

    private void filter(String text){
        filteredList.clear();
        for(Item_myDiary item : getSPJA(this)){   // for(변수 : 배열)  =>  배열에 들어 있는 값들을 하나씩 item변수에 대입시킨다.
            if(item.getDiaryText().toLowerCase().contains(text.toLowerCase()) && text.length()!=0){ // .toLowerCase()는 문자열을 소문자로 변환해서 반환하는 문법이다.
                                                                                // .contains()
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }

}
