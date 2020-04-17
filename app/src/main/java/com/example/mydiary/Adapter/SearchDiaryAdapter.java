package com.example.mydiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

import com.example.mydiary.Item.Item_myDiary;
import com.example.mydiary.R;
import com.example.mydiary.SearchDiaryActivity;

import java.util.ArrayList;

public class SearchDiaryAdapter extends RecyclerView.Adapter<SearchDiaryAdapter.ViewHolder> {

    private Context mContext;


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
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "수정하기");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제하기");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }


        // 컨텍스트 메뉴에서 항목을 클릭할 시에 동작을 설정한다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case 1001: // 수정

                        Intent intent = new Intent(mContext, SearchDiaryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("position", getAdapterPosition());
                        mContext.startActivity(intent);

                        break;

                    case 1002:  // 삭제

                        filteredList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), filteredList.size());

                        break;
                }
                return true;
            }
        };

    }

    public SearchDiaryAdapter(ArrayList<Item_myDiary> filteredList, Context mContext){

        this.filteredList = filteredList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public SearchDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mydiary, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchDiaryAdapter.ViewHolder viewHolder, int i) {

        if (!filteredList.get(i).diaryPicture.equals("")){
            viewHolder.iv_diaryPicture.setVisibility(View.VISIBLE);
            byte[] decodedByteArray = Base64.decode(filteredList.get(i).diaryPicture, Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            viewHolder.iv_diaryPicture.setImageBitmap(decodedBitmap);
        }

        viewHolder.tv_diaryDate.setText(filteredList.get(i).getDiaryDate());
        viewHolder.tv_diaryText.setText(filteredList.get(i).getDiaryText());


    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    ArrayList<Item_myDiary> filteredList = new ArrayList<>();

    public void filterList(ArrayList<Item_myDiary> filteredList){
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }


}
