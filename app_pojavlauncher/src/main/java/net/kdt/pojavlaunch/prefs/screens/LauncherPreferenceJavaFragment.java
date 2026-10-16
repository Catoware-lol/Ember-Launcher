package net.kdt.pojavlaunch.prefs.screens;

import static net.kdt.pojavlaunch.Architecture.is32BitsDevice;
import static net.kdt.pojavlaunch.Tools.getTotalDeviceMemory;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension;
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class LauncherPreferenceJavaFragment extends LauncherPreferenceFragment {
    private MultiRTConfigDialog mDialogScreen;
    private SwitchPreference mSwitchAutoJRE;
    private final ActivityResultLauncher<Object> mVmInstallLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("xz"), (data)->{
                if(data != null) Tools.installRuntimeFromUri(getContext(), data);
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwitchAutoJRE = findPreference("disable_autojre_select");
        mSwitchAutoJRE.setSummary("Stops automatic selection of which runtime to use in \"" + getString(R.string.main_install_jar_file) + "\"");

    }

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        int ramAllocation = LauncherPreferences.DEFAULT_PREF.getInt("allocation", LauncherPreferences.PREF_RAM_ALLOCATION);
        // Triggers a write for some reason
        addPreferencesFromResource(R.xml.pref_java);

        CustomSeekBarPreference memorySeekbar = requirePreference("allocation",
                CustomSeekBarPreference.class);

        int maxRAM;
        int deviceRam = getTotalDeviceMemory(memorySeekbar.getContext());

        if(is32BitsDevice() || deviceRam < 2048) maxRAM = Math.min(1024, deviceRam);
        else maxRAM = deviceRam - (deviceRam < 3064 ? 800 : 1024); //To have a minimum for the device to breathe

        memorySeekbar.setMaxKeepIncrement(maxRAM);
        memorySeekbar.setValue(ramAllocation);
        memorySeekbar.setSuffix(" MB");
        SwitchPreference autoRam = requirePreference("auto_configure_ram", SwitchPreference.class);
        memorySeekbar.setEnabled(!autoRam.isChecked());
        updateAutoSummary(autoRam);
        autoRam.setOnPreferenceChangeListener((preference, value) -> {
            boolean enabled = (Boolean) value;
            memorySeekbar.setEnabled(!enabled);
            LauncherPreferences.PREF_RAM_ALLOCATION = enabled
                    ? net.kdt.pojavlaunch.utils.DeviceMemory.recommendation(requireContext())
                    : memorySeekbar.getValue();
            updateAutoSummary(autoRam);
            return true;
        });

        EditTextPreference editJVMArgs = findPreference("javaArgs");
        if (editJVMArgs != null) {
            editJVMArgs.setOnBindEditTextListener(TextView::setSingleLine);
        }

        requirePreference("install_jre").setOnPreferenceClickListener(preference->{
            openMultiRTDialog();
            return true;
        });
    }

    private void updateAutoSummary(SwitchPreference preference) {
        preference.setSummary(getString(R.string.ember_auto_ram_summary) + "\n"
                + getString(R.string.ember_ram_estimate,
                net.kdt.pojavlaunch.utils.DeviceMemory.recommendation(requireContext())));
    }

    private void openMultiRTDialog() {
        if (mDialogScreen == null) {
            mDialogScreen = new MultiRTConfigDialog();
            mDialogScreen.prepare(getContext(), mVmInstallLauncher);
        }
        mDialogScreen.show();
    }
}
