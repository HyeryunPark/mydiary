package com.example.mydiary.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mydiary.BookmarkActivity;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.PublicDiaryActivity;
import com.example.mydiary.R;
import com.example.mydiary.model.DiaryModel;
import com.example.mydiary.sp.SharedMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PublicDiaryAdapter extends RecyclerView.Adapter<PublicDiaryAdapter.ViewHolder> {

    ArrayList<Item_myDiary> PublicDiary_items;

    String userEmail = "";
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    String saveDate;
    String nowUID, img_s;
    StorageReference mStorageReference;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        LinearLayout item_myDiary_linear;
        TextView tv_diaryDate, tv_diaryText;
        ImageView iv_diaryPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_myDiary_linear = itemView.findViewById(R.id.item_myDiary_linear);
            tv_diaryDate = (TextView) itemView.findViewById(R.id.tv_diaryDate);
            iv_diaryPicture = (ImageView) itemView.findViewById(R.id.iv_diaryPicture);
            tv_diaryText = (TextView) itemView.findViewById(R.id.tv_diaryText);

            itemView.setOnCreateContextMenuListener(this);  // OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해준다.
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해준다.
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 된다.

            MenuItem Bookmark = menu.add(Menu.NONE, 1001, 1, "담아가기");
            Bookmark.setOnMenuItemClickListener(onEditMenu);

            if (nowUID.equals(publicDiaryList.get(getAdapterPosition()).userUID)) {
                MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제하기");
                Delete.setOnMenuItemClickListener(onEditMenu);
            }


        }

        // 컨텍스트 메뉴에서 항목을 클릭할 시에 동작을 설정한다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

                switch (item.getItemId()) {
                    case 1001: // 담아가기
/*                        String pic = publicDiaryList.get(getAdapterPosition()).diaryPicture;
                        //==================================================================================
                        // SharedPreferences에 저장하기
                        // 로그인되어있는 아이디를 키값으로 저장하기 위해 저장되어있던 아이디 가져오기
                        SharedPreferences setting = itemView.getContext().getSharedPreferences("setting", MODE_PRIVATE);
                        String AutoLogin = setting.getString("autoLogin", "");
                        // 쪼개서 배열에 넣기
                        String[] login_info = AutoLogin.split(",");
                        String user_email = login_info[0];
                        String user_password = login_info[1];
                        //==================================================================================
                        // SharedPreferences
                        // bookMark.xml 이라는 이름의 SharedPreferences 파일을 가져온다. (없는 경우 자동생성)
                        SharedPreferences bookMark = itemView.getContext().getSharedPreferences("bookMark", MODE_PRIVATE);
                        // editor
                        SharedPreferences.Editor bookMark_editor = bookMark.edit();
                        // JSONArray
                        // key : 로그인된 이메일 , value : JSONObject
                        String getJSONArray = bookMark.getString(user_email, null);
                        JSONArray jsonArray = new JSONArray();
                        if (getJSONArray != null) {
                            try {
                                jsonArray = new JSONArray(getJSONArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // JSONObject
                        // key : 글 쓴 사람의 이메일, 날짜, 사진, 내용 , value :
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("이메일", user_email);
                            jsonArray.put(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("북마크 저장되니", bookMark + "");
                        bookMark_editor.putString(user_email, String.valueOf(jsonArray));
                        bookMark_editor.commit();

                        Log.d("야야야야야야", publicDiaryList.get(getAdapterPosition()).diaryPicture);

                        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(publicDiaryList.get(getAdapterPosition()).diaryPicture);

                        final long ONE_MEGABYTE = 1024 * 1024;
                        mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // 성공
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                Log.d("스토리지 다운", "성공");
                                img_s = getBase64String(bitmap);
                                Log.d("스토리지 비트맵", img_s + "");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 실패
                                Log.d("스토리지 다운", "실패");

                            }
                        });

                        Item_myDiary item_myDiary = new Item_myDiary(img_s, publicDiaryList.get(getAdapterPosition()).diaryDate, publicDiaryList.get(getAdapterPosition()).diaryText);
                        item_myDiary.userEmail = publicDiaryList.get(getAdapterPosition()).userUID;
                        BookmarkActivity.Bookmark_items.add(item_myDiary);*/

                        // firebase에 저장하기
                        Query query = mDatabase.getReference("PublicDiary").child(saveDate).child(publicDiaryList.get(getAdapterPosition()).userUID);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DiaryModel diaryModel = dataSnapshot.getValue(DiaryModel.class);

                                mDatabase.getReference("BookMark").child(nowUID).child(saveDate+publicDiaryList.get(getAdapterPosition()).userUID).setValue(diaryModel);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;

                    case 1002:  // 삭제하기

                        mDatabase.getReference().child("PublicDiary").child(saveDate).child(nowUID).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                floatingActionButton.show();

                                Toast.makeText(itemView.getContext(), "공개한글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                               /* SharedMethod.deletePublicDiary(itemView.getContext(),getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), PublicDiary_items.size());*/
                            }
                        });
                        break;
                }
                return true;
            }
        };
    }

    public String getBase64String(Bitmap bitmap) {  // 비트맵형태의 이미지를 스트링으로 바꿔주는 메서드
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }


    List<DiaryModel> publicDiaryList;
    FloatingActionButton floatingActionButton;
    Context context;

    public PublicDiaryAdapter(Context context, FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
        this.context = context;

        this.PublicDiary_items = SharedMethod.getPublicDiary(context);
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
        this.userEmail = user_email;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        // 현재날짜 구하기 ---------------------------------------------------------------------------
        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int current_month = month + 1;
        int current_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

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
        saveDate = date_year + date_month + date_day;
        // -----------------------------------------------------------------------------------------
        publicDiaryList = new ArrayList<>();
        Query query = mDatabase.getReference("PublicDiary").child(saveDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicDiaryList.clear();
                Log.d("공유로그", String.valueOf(dataSnapshot));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("일기공유 snapshot", snapshot.toString());
                    publicDiaryList.add(snapshot.getValue(DiaryModel.class));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public PublicDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mydiary, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicDiaryAdapter.ViewHolder viewHolder, int i) {

        nowUID = mAuth.getCurrentUser().getUid();    // 현재 로그인한 유저의 UID
/*
        if (PublicDiary_items.get(i).userEmail != ""){      // 내가 올린 글에 배경색 바꾸기
            if (PublicDiary_items.get(i).userEmail.equals(userEmail)){
                viewHolder.itemView.setBackgroundColor(Color.rgb(249,240,222));
            }
        }
            if (!PublicDiary_items.get(i).diaryPicture.equals("")){
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);
            byte[] decodedByteArray = Base64.decode(PublicDiary_items.get(i).diaryPicture, Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            viewHolder.iv_diaryPicture.setImageBitmap(decodedBitmap);
        }
        viewHolder.tv_diaryDate.setText(PublicDiary_items.get(i).getDiaryDate());
        viewHolder.tv_diaryText.setText(PublicDiary_items.get(i).getDiaryText());           shared쓸때 값 뿌려주던 코드들
*/

        // 내가 쓴 글 티나게 배경 바꾸기
        if (nowUID.equals(publicDiaryList.get(i).userUID)) {
            viewHolder.itemView.setBackgroundColor(Color.rgb(160, 200, 150));
        }

        if (!publicDiaryList.get(i).diaryPicture.equals("")) {
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);

            Glide.with(viewHolder.itemView.getContext()).load(publicDiaryList.get(i).diaryPicture).into(viewHolder.iv_diaryPicture);
        }
        viewHolder.tv_diaryDate.setText(publicDiaryList.get(i).diaryDate);
        viewHolder.tv_diaryText.setText(publicDiaryList.get(i).diaryText);
    }

    @Override
    public int getItemCount() {
        return publicDiaryList.size();
    }

}
