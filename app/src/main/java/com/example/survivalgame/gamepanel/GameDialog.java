package com.example.survivalgame.gamepanel;

import android.app.AlertDialog;
import android.content.Context;

public class GameDialog extends AlertDialog {
    protected GameDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
