package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import android.graphics.*;
import android.widget.EditText;

public class MineEditText extends androidx.appcompat.widget.AppCompatEditText {
	public MineEditText(Context ctx) {
		super(ctx);
		init();
	}

	public MineEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

    public void init() {
        setBackgroundResource(net.kdt.pojavlaunch.R.drawable.background_line);
        setTextColor(getResources().getColor(net.kdt.pojavlaunch.R.color.primary_text));
        setHintTextColor(getResources().getColor(net.kdt.pojavlaunch.R.color.secondary_text));
        int padding = Math.round(14 * getResources().getDisplayMetrics().density);
        setPadding(padding, padding, padding, padding);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }
}
