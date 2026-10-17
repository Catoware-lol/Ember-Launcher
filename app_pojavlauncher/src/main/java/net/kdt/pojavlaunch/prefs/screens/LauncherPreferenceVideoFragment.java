package net.kdt.pojavlaunch.prefs.screens;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

/**
 * Fragment for any settings video related
 */
public class LauncherPreferenceVideoFragment extends LauncherPreferenceFragment {
    private androidx.appcompat.app.AlertDialog overlayPreviewDialog;
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_video);
        requirePreference("fps_preview").setOnPreferenceClickListener(preference -> {
            showOverlayPreview();
            return true;
        });
        int resolution = (int) (LauncherPreferences.PREF_SCALE_FACTOR * 100);

        //Disable notch checking behavior on android 8.1 and below.
        requirePreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && LauncherPreferences.PREF_NOTCH_SIZE > 0);

        CustomSeekBarPreference resolutionSeekbar = requirePreference("resolutionRatio",
                CustomSeekBarPreference.class);
        resolutionSeekbar.setSuffix(" %");

        // #724 bug fix
        if (resolution < 25) {
            resolutionSeekbar.setValue(100);
        } else {
            resolutionSeekbar.setValue(resolution);
        }

        // Sustained performance is only available since Nougat
        SwitchPreference sustainedPerfSwitch = requirePreference("sustainedPerformance",
                SwitchPreference.class);
        sustainedPerfSwitch.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
        sustainedPerfSwitch.setChecked(LauncherPreferences.PREF_SUSTAINED_PERFORMANCE);

        requirePreference("alternate_surface", SwitchPreferenceCompat.class).setChecked(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);
        requirePreference("force_vsync", SwitchPreferenceCompat.class).setChecked(LauncherPreferences.PREF_FORCE_VSYNC);

        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void showOverlayPreview() {
        android.widget.LinearLayout content = new android.widget.LinearLayout(requireContext());
        content.setOrientation(android.widget.LinearLayout.VERTICAL);
        android.widget.FrameLayout canvas = new android.widget.FrameLayout(requireContext());
        content.addView(canvas, new android.widget.LinearLayout.LayoutParams(-1,
                Math.round(170 * getResources().getDisplayMetrics().density)));
        android.widget.TextView note = new android.widget.TextView(requireContext());
        int pad = Math.round(16 * getResources().getDisplayMetrics().density);
        note.setPadding(pad, 0, pad, pad);
        note.setText(R.string.ember_overlay_preview_note);
        content.addView(note);
        net.kdt.pojavlaunch.utils.PerformanceOverlay overlay =
                new net.kdt.pojavlaunch.utils.PerformanceOverlay(canvas);
        overlayPreviewDialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.ember_overlay_preview).setView(content)
                .setPositiveButton(android.R.string.ok, null).create();
        overlayPreviewDialog.setOnDismissListener(dialog -> overlay.stop());
        overlayPreviewDialog.show();
        overlay.start();
    }

    @Override public void onStop() {
        if (overlayPreviewDialog != null) overlayPreviewDialog.dismiss();
        super.onStop();
    }

    private void computeVisibility(){
        requirePreference("force_vsync", SwitchPreferenceCompat.class)
                .setVisible(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);
    }
}
