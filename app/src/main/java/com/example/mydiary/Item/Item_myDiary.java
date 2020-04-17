package com.example.mydiary.Item;

public class Item_myDiary {
    public String diaryPicture;
    public String diaryDate;
    public String diaryText;
    public String userEmail;

    public Item_myDiary(String diaryPicture, String diaryDate, String diaryText) {
        this.diaryPicture = diaryPicture;
        this.diaryDate = diaryDate;
        this.diaryText = diaryText;
        userEmail = "";
    }

    public String getDiaryPicture() {
        return diaryPicture;
    }

    public String getDiaryDate() {
        return diaryDate;
    }

    public String getDiaryText() {
        return diaryText;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
