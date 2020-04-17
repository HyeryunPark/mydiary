package com.example.mydiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import com.example.mydiary.DiaryCollectionActivity;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.R;
import com.example.mydiary.WriteActivity;
import com.example.mydiary.sp.SharedMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyDiaryAdapter extends RecyclerView.Adapter<MyDiaryAdapter.ViewHolder> {

    ArrayList<Item_myDiary> Diary_items;
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;


    // 컨텍스트 메뉴를 사용하려면 RecyclerView.ViewHolder를 상속받은 클래스에서
    // OnCreateContextMenuListener 리스너를 구현해야 합니다.

    public MyDiaryAdapter(ArrayList<Item_myDiary> Diary_items, Context mContext) {
        this.Diary_items = Diary_items;
        this.mContext = mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        LinearLayout item_myDiary_linear;
        TextView tv_diaryDate, tv_diaryText;
        ImageView iv_diaryPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_myDiary_linear = (LinearLayout) itemView.findViewById(R.id.item_myDiary_linear);
            tv_diaryDate = (TextView) itemView.findViewById(R.id.tv_diaryDate);
            iv_diaryPicture = (ImageView) itemView.findViewById(R.id.iv_diaryPicture);
            tv_diaryText = (TextView) itemView.findViewById(R.id.tv_diaryText);

            itemView.setOnCreateContextMenuListener(this);  // OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해준다.

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해준다.
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 된다.

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "수정하기");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제하기");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 컨텍스트 메뉴에서 항목을 클릭할 시에 동작을 설정한다.
        private final MenuItem.OnMenuItemClickListener onEditMenu;

        {
            onEditMenu = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case 1001: // 수정

                            Intent intent = new Intent(mContext, WriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("position", getAdapterPosition());
                            mContext.startActivity(intent);

                            break;

                        case 1002:  // 삭제
                            mAuth = FirebaseAuth.getInstance();
                            mDatabase = FirebaseDatabase.getInstance();
                            mStorageRef = FirebaseStorage.getInstance().getReference();

                            final String nowUID = mAuth.getCurrentUser().getUid();    // 로그인한 현재 유저의 UID

                            // 쪼개서 배열에 넣고 합치기
                            String[] date = DiaryCollectionActivity.Diary_items.get(getAdapterPosition()).diaryDate.split(". ");
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

                            mStorageRef.child("storyImage").child(saveDate).child(nowUID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.getReference().child("Diarys").child(nowUID).child(saveDate).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            mDatabase.getReference().child("PublicDiary").child(saveDate).child(nowUID).setValue("");   // 내 다이어리 지우면 공유한 일기도 지우기
                                            Toast.makeText(mContext, "삭제완료됐습니다.", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "스토리지 삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            });


                            SharedMethod.deleteSharedPreferences(mContext, getAdapterPosition());
                            DiaryCollectionActivity.Diary_items.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), Diary_items.size());


                            break;
                    }
                    return true;
                }
            };
        }

    }


    @NonNull
    @Override
    public MyDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mydiary, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyDiaryAdapter.ViewHolder viewHolder, final int i) {


        if (!Diary_items.get(i).diaryPicture.equals("")) {
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);
            byte[] decodedByteArray = Base64.decode(Diary_items.get(i).diaryPicture, Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            viewHolder.iv_diaryPicture.setImageBitmap(decodedBitmap);
        }

        viewHolder.tv_diaryDate.setText(Diary_items.get(i).getDiaryDate());
        viewHolder.tv_diaryText.setText(Diary_items.get(i).getDiaryText());

    }


    @Override
    public int getItemCount() {

        return Diary_items.size();
    }


}
