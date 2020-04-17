package com.example.mydiary.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mydiary.ChangeSharedItem;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.PublicDiaryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SharedMethod {


    public static ArrayList<Item_myDiary> getSPJA(Context context) {
        //==================================================================================
        // SharedPreferences
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences
        // diary.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
        SharedPreferences diary = context.getSharedPreferences("diary", MODE_PRIVATE);
        // key값 : 로그인된 아이디 , value 값 : 날짜, 사진, 내용
        String getJSONArray = diary.getString(user_email, null);

        ArrayList<Item_myDiary> listdata = new ArrayList<>();
        if (getJSONArray != null) {
            try {
                JSONArray jArray = new JSONArray(getJSONArray);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        String pic = obj.getString("사진");
                        String date = obj.getString("날짜");
                        String content = obj.getString("내용");

                        listdata.add(new Item_myDiary(pic, date, content));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listdata;
    }


    // 선택한 날짜의 정보와 배열에서의 순서를 알려주는 메서드
    public static ChangeSharedItem selectDate2item(Context context, String selectDate) {

        ArrayList<Item_myDiary> item_myDiaries = getSPJA(context);
        int n = 0;

        for (Item_myDiary item : item_myDiaries) {

            if (item.getDiaryDate().equals(selectDate)) {

                ChangeSharedItem changeSharedItem = new ChangeSharedItem();
                changeSharedItem.item_myDiary = item;
                changeSharedItem.n = n;

                return changeSharedItem;
            }
            n++;
        }
        return null;
    }




    public static void deleteSharedPreferences(Context context, int position) {
        //==================================================================================
        // SharedPreferences에 저장하기
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences
        // diary.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
        SharedPreferences diary = context.getSharedPreferences("diary", MODE_PRIVATE);
        // editor
        SharedPreferences.Editor diary_editor = diary.edit();
        // JSONArray
        // key : 로그인된 이메일 , value : JSONObject
        String getJSONArray = diary.getString(user_email, null);
        JSONArray jsonArray = new JSONArray();
        if (getJSONArray != null) {
            try {
                jsonArray = new JSONArray(getJSONArray);
                jsonArray.remove(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        diary_editor.putString(user_email, String.valueOf(jsonArray));
        diary_editor.commit();
    }


    // 일기 공유에 추가하는 메서드
    public static ArrayList<Item_myDiary> getPublicDiary(Context context) {
        //==================================================================================
        // SharedPreferences
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences
        // publicDiary.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
        SharedPreferences publicDiary = context.getSharedPreferences("publicDiary", MODE_PRIVATE);
        // key값 : 로그인된 아이디 , value 값 : 날짜, 사진, 내용
        Map<String, ?> getPD = publicDiary.getAll();

        ArrayList<Item_myDiary> listdata = new ArrayList<>();


        for (Map.Entry<String, ?> entry : getPD.entrySet()) {
            String key_email = entry.getKey();
            String value_diary = entry.getValue().toString();
            Item_myDiary item_myDiary = null;
            try {
                JSONObject jsonObject = new JSONObject(value_diary);
                item_myDiary = new Item_myDiary(jsonObject.getString("사진"), jsonObject.getString("날짜"), jsonObject.getString("내용"));
                item_myDiary.userEmail = key_email;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listdata.add(item_myDiary);
        }


        return listdata;
    }


    public static void deletePublicDiary(Context context, int position) {
        //==================================================================================
        // SharedPreferences에 저장하기
        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
        SharedPreferences setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        String AutoLogin = setting.getString("autoLogin", "");
        // 쪼개서 배열에 넣기
        String[] login_info = AutoLogin.split(",");
        String user_email = login_info[0];
        String user_password = login_info[1];
        //==================================================================================
        // SharedPreferences에 저장하기
        // publicDiary.xml 이라는 이름의 SharedPreferences 파일을 가져온다 (없을 경우 자동생성)
        SharedPreferences publicDiary = context.getSharedPreferences("publicDiary", MODE_PRIVATE);
        // editor
        SharedPreferences.Editor editor = publicDiary.edit();

        editor.remove(user_email);
        editor.commit();



    }


}
