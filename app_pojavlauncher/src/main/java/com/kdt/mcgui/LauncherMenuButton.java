package com.kdt.mcgui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;


import net.kdt.pojavlaunch.R;

import fr.spse.extended_view.ExtendedButton;

public class LauncherMenuButton extends ExtendedButton {

    public LauncherMenuButton(@NonNull Context context) {
        super(context);
        setSettings();
    }
    public LauncherMenuButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSettings();
    }


    private void setSettings() {
        float density = getResources().getDisplayMetrics().density;
        int padding = Math.round(10 * density);
        setBackgroundResource(R.drawable.ember_tool_button);
        setTextColor(getResources().getColor(R.color.primary_text));
        setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        setAllCaps(false);
        setCompoundDrawablePadding(Math.round(6 * density));
        setPaddingRelative(padding, 0, padding, 0);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        setMinWidth(0);
        int[] sizes = getExtendedViewData().getSizeCompounds();
        sizes[0] = Math.round(18 * density);
        getExtendedViewData().setSizeCompounds(sizes);
        postProcessDrawables();
    }
}
