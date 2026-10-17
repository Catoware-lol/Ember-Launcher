package net.kdt.pojavlaunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.PopupMenu;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlJoystickData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.customcontrols.EditorExitable;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import java.io.IOException;

public class CustomControlsActivity extends BaseActivity implements EditorExitable {
    private ControlLayout mControlLayout;
    private boolean preview;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_custom_controls);
        mControlLayout = findViewById(R.id.customctrl_controllayout);
        mControlLayout.setModifiable(true);
        findViewById(R.id.editor_back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.editor_add).setOnClickListener(v -> showActions(v, true));
        findViewById(R.id.editor_more).setOnClickListener(v -> showActions(v, false));
        findViewById(R.id.editor_save).setOnClickListener(v -> mControlLayout.openSaveDialog(null));
        findViewById(R.id.editor_preview).setOnClickListener(v -> setPreview(true));
        findViewById(R.id.editor_show_tools).setOnClickListener(v -> setPreview(false));
        try { mControlLayout.loadLayout(LauncherPreferences.PREF_DEFAULTCTRL_PATH); }
        catch (IOException e) { Tools.showError(this, e); }
    }
    private void setPreview(boolean enabled) {
        preview = enabled;
        mControlLayout.removeEditWindow();
        findViewById(R.id.editor_toolbar).setVisibility(enabled ? View.GONE : View.VISIBLE);
        findViewById(R.id.editor_preview_shield).setVisibility(enabled ? View.VISIBLE : View.GONE);
        findViewById(R.id.editor_show_tools).setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
    private void showActions(View anchor, boolean add) {
        String[] actions = getResources().getStringArray(R.array.menu_customcontrol_customactivity);
        PopupMenu menu = new PopupMenu(this, anchor);
        for (int i : add ? new int[]{0, 1, 2} : new int[]{3, 5, 6})
            menu.getMenu().add(0, i, i, actions[i]);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0: mControlLayout.addControlButton(new ControlData("New")); break;
                case 1: mControlLayout.addDrawer(new ControlDrawerData()); break;
                case 2: mControlLayout.addJoystickButton(new ControlJoystickData()); break;
                case 3: mControlLayout.openLoadDialog(); break;
                case 5: mControlLayout.openSetDefaultDialog(); break;
                case 6: exportLayout(); break;
            }
            return true;
        });
        menu.show();
    }
    private void exportLayout() {
        try {
            Uri uri = DocumentsContract.buildDocumentUri(getString(R.string.storageProviderAuthorities),
                    mControlLayout.saveToDirectory(mControlLayout.mLayoutFileName));
            Intent share = new Intent(Intent.ACTION_SEND).setType("application/json")
                    .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, mControlLayout.mLayoutFileName));
        } catch (Exception e) { Tools.showError(this, e); }
    }
    @Override public void onBackPressed() {
        if (preview) { setPreview(false); return; }
        mControlLayout.askToExit(this);
    }
    @Override public void exitEditor() { super.onBackPressed(); }
}
