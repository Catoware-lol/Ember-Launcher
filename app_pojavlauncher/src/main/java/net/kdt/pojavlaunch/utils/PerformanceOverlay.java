package net.kdt.pojavlaunch.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import java.util.Locale;

/** In-app overlay. Counts game bridge swaps, never Android UI refreshes. */
public final class PerformanceOverlay {
    private final TextView text;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final FrameRateSample sample = new FrameRateSample();
    private boolean running;
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!running) return;
            double fps = sample.sample(JREUtils.getPresentedFrameCount(), SystemClock.elapsedRealtimeNanos());
            String value = fps > 0 ? String.format(Locale.US, "FPS  %5.1f", fps) : "FPS     —";
            if (LauncherPreferences.DEFAULT_PREF.getBoolean("fps_details", false)) {
                value += fps > 0 ? String.format(Locale.US, "\nAVG  %5.2f ms", 1000 / fps) : "\nAVG      — ms";
                value += "\nPROC " + Debug.getPss() / 1024 + " MiB PSS";
                value += "\nFREE " + DeviceMemory.snapshot(text.getContext()).availMem / 1048576 + " MiB";
            }
            text.setText(value);
            handler.postDelayed(this, 1000);
        }
    };
    public PerformanceOverlay(FrameLayout parent) {
        text = new TextView(parent.getContext());
        float density = parent.getResources().getDisplayMetrics().density;
        text.setTextColor(Color.rgb(255, 185, 95));
        text.setTextSize(12);
        text.setTypeface(Typeface.MONOSPACE);
        text.setBackgroundColor(0xC018191B);
        int pad = (int) (8 * density);
        text.setPadding(pad, pad / 2, pad, pad / 2);
        text.setClickable(false);
        text.setFocusable(false);
        text.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.START);
        params.setMargins(pad, (int) (32 * density), pad, pad);
        parent.addView(text, params);
        text.setVisibility(View.GONE);
    }
    public void start() {
        stop();
        if (!LauncherPreferences.DEFAULT_PREF.getBoolean("fps_counter", false)) return;
        running = true;
        JREUtils.setFrameCounting(true);
        sample.reset(JREUtils.getPresentedFrameCount(), SystemClock.elapsedRealtimeNanos());
        text.setText("FPS     —");
        text.setVisibility(View.VISIBLE);
        handler.postDelayed(tick, 1000);
    }
    public void stop() {
        running = false;
        handler.removeCallbacks(tick);
        JREUtils.setFrameCounting(false);
        text.setVisibility(View.GONE);
    }
}
