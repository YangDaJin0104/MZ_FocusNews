package com.example.mz_focusnews.Ranking;

import android.widget.PopupWindow;

// RankingFragment -> QuizFragment PopupWindow 객체를 넘겨주기 위한 Singleton 클래스 (dismiss() 하기위함)
public class PopupManager {
    private static PopupManager instance;
    private PopupWindow popupWindow;

    private PopupManager() {

    }

    public static synchronized PopupManager getInstance() {
        if (instance == null) {
            instance = new PopupManager();
        }
        return instance;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
