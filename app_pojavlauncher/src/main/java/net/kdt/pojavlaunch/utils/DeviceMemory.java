package net.kdt.pojavlaunch.utils;

import android.app.ActivityManager;
import android.content.Context;
import net.kdt.pojavlaunch.Architecture;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public final class DeviceMemory {
    private DeviceMemory() {}
    public static ActivityManager.MemoryInfo snapshot(Context context) {
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(info);
        return info;
    }
    public static int recommendation(Context context) {
        ActivityManager.MemoryInfo info = snapshot(context);
        return MemoryBudget.recommend(info.totalMem / 1048576, info.availMem / 1048576,
                info.threshold / 1048576, Architecture.is32BitsDevice());
    }
    public static void refresh(Context context) {
        if (LauncherPreferences.DEFAULT_PREF.getBoolean("auto_configure_ram", false)) {
            LauncherPreferences.PREF_RAM_ALLOCATION = recommendation(context);
        }
    }
}
