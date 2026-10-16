package com.kdt.mcgui;

import android.content.*;
import android.graphics.*;
import android.util.*;

import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.R;

public class MineButton extends androidx.appcompat.widget.AppCompatButton {
	
	public MineButton(Context ctx) {
		this(ctx, null);
	}
	
	public MineButton(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

    public void init() {
        boolean primary = "ember_primary".equals(getTag());
        setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        setBackgroundResource(primary ? R.drawable.ember_primary_button : R.drawable.mine_button_background);
        setTextColor(new android.content.res.ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled}, new int[]{}},
                new int[]{getResources().getColor(R.color.secondary_text),
                        getResources().getColor(primary ? R.color.ember_ink : R.color.primary_text)}));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setAllCaps(false);
        setMinHeight(dp(48));
        setPadding(dp(16), dp(10), dp(16), dp(10));
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
