package com.example.mydiary.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.R;
import com.example.mydiary.model.DiaryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    ArrayList<Item_myDiary> Bookmark_items;
    private Context mContext;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    String nowUID;

    List<DiaryModel> bookMarkList;
    public BookmarkAdapter( ArrayList<Item_myDiary> Bookmark_items, Context mContext){
        this.Bookmark_items = Bookmark_items;
        this.mContext = mContext;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        nowUID = mAuth.getCurrentUser().getUid();   // 현재 로그인한 유저의 UID


        bookMarkList = new ArrayList<>();
        Query query = mDatabase.getReference("BookMark").child(nowUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookMarkList.clear();
                Log.d("공유로그", String.valueOf(dataSnapshot));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("일기공유 snapshot", snapshot.toString());
                    bookMarkList.add(snapshot.getValue(DiaryModel.class));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_myDiary_linear;
        TextView tv_diaryDate, tv_diaryText;
        ImageView iv_diaryPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_myDiary_linear = itemView.findViewById(R.id.item_myDiary_linear);
            tv_diaryDate = (TextView) itemView.findViewById(R.id.tv_diaryDate);
            iv_diaryPicture = (ImageView) itemView.findViewById(R.id.iv_diaryPicture);
            tv_diaryText = (TextView) itemView.findViewById(R.id.tv_diaryText);


        }
    }


    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mydiary, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder viewHolder, final int i) {

/*        if (!Bookmark_items.get(i).diaryPicture.equals("")){
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);
            byte[] decodedByteArray = Base64.decode(Bookmark_items.get(i).diaryPicture, Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            viewHolder.iv_diaryPicture.setImageBitmap(decodedBitmap);
        }
        viewHolder.tv_diaryDate.setText(Bookmark_items.get(i).getDiaryDate());
        viewHolder.tv_diaryText.setText(Bookmark_items.get(i).getDiaryText());*/

        if (!bookMarkList.get(i).diaryPicture.equals("")) {
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);

            Glide.with(viewHolder.itemView.getContext()).load(bookMarkList.get(i).diaryPicture).into(viewHolder.iv_diaryPicture);
        }
        viewHolder.tv_diaryDate.setText(bookMarkList.get(i).diaryDate);
        viewHolder.tv_diaryText.setText(bookMarkList.get(i).diaryText);



        // 클릭이벤트 주기
        // 길게 누르면 삭제하기
        viewHolder.item_myDiary_linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder dig = new AlertDialog.Builder(v.getContext());
                dig.setTitle("북마크 삭제")
                        .setMessage("담아온글을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

/*                                Bookmark_items.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, Bookmark_items.size());*/

                                String diarydate = bookMarkList.get(i).diaryDate;
                                String[] date = diarydate.split(". ");
                                String date_year = date[0];
                                String date_month = date[1];
                                String date_day = date[2];
                                if (Integer.parseInt(date_month) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
                                    date_month = "0" + date_month;
                                }
                                if (Integer.parseInt(date_day) < 10) {  // 월, 일이 10보다 작으면 앞에 0 붙인다.
                                    date_day = "0" + date_day;
                                }
                                String coiceDate = date_year + date_month + date_day;

                                mDatabase.getReference().child("BookMark").child(nowUID).child(coiceDate+bookMarkList.get(i).userUID).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                        Toast.makeText(v.getContext(), "북마크가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = dig.create();
                dialog.show();

                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return bookMarkList.size();
    }

}
