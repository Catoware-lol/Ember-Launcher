package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import net.kdt.pojavlaunch.R;
import java.util.Locale;

/** Short diagnostics, never presented as Minecraft frame rate or a gaming score. */
public final class DeviceDiagnostics {
    private static volatile long sink;
    private DeviceDiagnostics() {}

    public static void show(Activity activity) {
        TextView report = new TextView(activity);
        int pad = (int) (20 * activity.getResources().getDisplayMetrics().density);
        report.setPadding(pad, pad, pad, pad);
        report.setTextSize(14);
        report.setTextIsSelectable(true);
        android.widget.ScrollView scroll = new android.widget.ScrollView(activity);
        scroll.addView(report);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.ember_device).setView(scroll)
                .setPositiveButton(R.string.ember_benchmark, null)
                .setNeutralButton("Refresh", null)
                .setNegativeButton(android.R.string.cancel, null).create();
        dialog.setOnShowListener(ignored -> {
            report.setText(deviceReport(activity) + "\n\nGPU: detecting…");
            new Thread(() -> {
                String gpu = gpuReport();
                activity.runOnUiThread(() -> {
                    if (!dialog.isShowing() || activity.isFinishing() || activity.isDestroyed()) return;
                    report.setText(deviceReport(activity) + "\n\n" + gpu);
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v ->
                            report.setText(deviceReport(activity) + "\n\n" + gpu));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        report.setText(deviceReport(activity) + "\n\n" + gpu + "\n\n"
                                + activity.getString(R.string.ember_benchmark_running));
                        new Thread(() -> {
                            String result = benchmark();
                            activity.runOnUiThread(() -> {
                                if (!dialog.isShowing() || activity.isFinishing() || activity.isDestroyed()) return;
                                report.setText(deviceReport(activity) + "\n\n" + gpu + "\n\n" + result);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            });
                        }, "Ember-benchmark").start();
                    });
                });
            }, "Ember-GPU-report").start();
        });
        dialog.show();
    }

    private static String deviceReport(Activity activity) {
        ActivityManager.MemoryInfo memory = DeviceMemory.snapshot(activity);
        return Build.MANUFACTURER + " " + Build.MODEL
                + "\nAndroid " + Build.VERSION.RELEASE + " · API " + Build.VERSION.SDK_INT
                + "\nABI: " + android.text.TextUtils.join(", ", Build.SUPPORTED_ABIS)
                + "\nCPU: " + Runtime.getRuntime().availableProcessors() + " logical cores"
                + "\nRAM: " + memory.totalMem / 1048576 + " MB total · "
                + memory.availMem / 1048576 + " MB available"
                + "\nMemory pressure: " + (memory.lowMemory ? "High — close unused apps before playing" : "Normal")
                + "\nAuto RAM estimate: " + DeviceMemory.recommendation(activity) + " MB"
                + "\nAvailable RAM includes the effect of other apps. Android limits per-app visibility.";
    }

    private static String gpuReport() {
        EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        EGLContext context = EGL14.EGL_NO_CONTEXT;
        EGLSurface surface = EGL14.EGL_NO_SURFACE;
        try {
            int[] version = new int[2];
            if (!EGL14.eglInitialize(display, version, 0, version, 1)) return "GPU: unavailable (EGL initialization failed)";
            EGLConfig[] configs = new EGLConfig[1];
            int[] count = new int[1];
            int[] attributes = {EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE};
            if (!EGL14.eglChooseConfig(display, attributes, 0, configs, 0, 1, count, 0) || count[0] == 0)
                return "GPU: unavailable (no offscreen GLES configuration)";
            context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT,
                    new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE}, 0);
            surface = EGL14.eglCreatePbufferSurface(display, configs[0],
                    new int[]{EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE}, 0);
            if (!EGL14.eglMakeCurrent(display, surface, surface, context)) return "GPU: unavailable (context creation failed)";
            return "GPU: " + GLES20.glGetString(GLES20.GL_RENDERER)
                    + "\nVendor: " + GLES20.glGetString(GLES20.GL_VENDOR)
                    + "\nDriver: " + GLES20.glGetString(GLES20.GL_VERSION)
                    + "\nThis is Android’s GLES driver; Minecraft’s selected renderer may differ.";
        } catch (RuntimeException e) {
            return "GPU: unavailable (" + e.getClass().getSimpleName() + ")";
        } finally {
            if (display != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                if (surface != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(display, surface);
                if (context != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(display, context);
                EGL14.eglTerminate(display);
                EGL14.eglReleaseThread();
            }
        }
    }

    private static String benchmark() {
        // Warm up the same operation before the timed, single-thread run.
        long value = 123456789;
        for (int i = 0; i < 200000; i++) value = mix(value);
        long start = System.nanoTime();
        long end = start + 1000000000L;
        long operations = 0;
        do {
            for (int i = 0; i < 10000; i++) value = mix(value);
            operations += 10000;
        } while (System.nanoTime() < end);
        double seconds = (System.nanoTime() - start) / 1e9;
        sink = value;
        try {
            byte[] from = new byte[4 * 1024 * 1024];
            byte[] to = new byte[from.length];
            java.util.Arrays.fill(from, (byte) value);
            long copies = 0;
            start = System.nanoTime();
            end = start + 1000000000L;
            do {
                System.arraycopy(from, 0, to, 0, from.length);
                sink += to[(int) (copies % to.length)];
                copies++;
            } while (System.nanoTime() < end);
            double copySeconds = (System.nanoTime() - start) / 1e9;
            return String.format(Locale.US,
                    "Benchmark complete\nCPU: %.1f million integer operations/s (one thread)\nMemory copy: %.0f MiB/s (4 MiB blocks)\n\nA short synthetic test, not Minecraft FPS. Results vary with temperature and background activity. GPU performance is not measured.",
                    operations / seconds / 1e6, copies * 4 / copySeconds);
        } catch (OutOfMemoryError e) {
            return "Benchmark stopped: not enough memory for the 8 MiB copy test.";
        }
    }

    private static long mix(long value) {
        value ^= value << 13;
        value ^= value >>> 7;
        return value ^ (value << 17);
    }
}
