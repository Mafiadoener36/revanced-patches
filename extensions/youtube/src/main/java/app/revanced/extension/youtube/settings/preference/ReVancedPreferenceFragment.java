package app.revanced.extension.youtube.settings.preference;

import static app.revanced.extension.shared.Utils.getResourceIdentifier;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;

import app.revanced.extension.shared.Logger;
import app.revanced.extension.shared.Utils;
import app.revanced.extension.shared.settings.preference.AbstractPreferenceFragment;
import app.revanced.extension.youtube.ThemeHelper;
import app.revanced.extension.youtube.patches.playback.speed.CustomPlaybackSpeedPatch;
import app.revanced.extension.youtube.settings.Settings;

/**
 * Preference fragment for ReVanced settings.
 *
 * @noinspection deprecation
 */
public class ReVancedPreferenceFragment extends AbstractPreferenceFragment {

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getBackButtonDrawable() {
        final int backButtonResource = getResourceIdentifier(ThemeHelper.isDarkTheme()
                        ? "yt_outline_arrow_left_white_24"
                        : "yt_outline_arrow_left_black_24",
                "drawable");
        return Utils.getContext().getResources().getDrawable(backButtonResource);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initialize() {
        super.initialize();

        try {
            setPreferenceScreenToolbar(getPreferenceScreen());

            // If the preference was included, then initialize it based on the available playback speed.
            Preference defaultSpeedPreference = findPreference(Settings.PLAYBACK_SPEED_DEFAULT.key);
            if (defaultSpeedPreference instanceof ListPreference) {
                CustomPlaybackSpeedPatch.initializeListPreference((ListPreference) defaultSpeedPreference);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    private void setPreferenceScreenToolbar(PreferenceScreen parentScreen) {
        for (int i = 0, preferenceCount = parentScreen.getPreferenceCount(); i < preferenceCount; i++) {
            Preference childPreference = parentScreen.getPreference(i);
            if (childPreference instanceof PreferenceScreen) {
                // Recursively set sub preferences.
                setPreferenceScreenToolbar((PreferenceScreen) childPreference);

                childPreference.setOnPreferenceClickListener(
                        childScreen -> {
                            Dialog preferenceScreenDialog = ((PreferenceScreen) childScreen).getDialog();
                            ViewGroup rootView = (ViewGroup) preferenceScreenDialog
                                    .findViewById(android.R.id.content)
                                    .getParent();

                            // Fix required for Android 15 and YT 19.45+
                            // FIXME:
                            // On Android 15 the text layout is not aligned the same as the parent
                            // screen and it looks a little off.  Otherwise this works.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                                    Insets statusInsets = insets.getInsets(WindowInsets.Type.statusBars());
                                    v.setPadding(0, statusInsets.top, 0, 0);
                                    return insets;
                                });
                            }

                            Toolbar toolbar = new Toolbar(childScreen.getContext());
                            toolbar.setTitle(childScreen.getTitle());
                            toolbar.setNavigationIcon(getBackButtonDrawable());
                            toolbar.setNavigationOnClickListener(view -> preferenceScreenDialog.dismiss());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                final int margin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()
                                );
                                toolbar.setTitleMargin(margin, 0, margin, 0);
                            }

                            TextView toolbarTextView = Utils.getChildView(toolbar,
                                    true, TextView.class::isInstance);
                            if (toolbarTextView != null) {
                                toolbarTextView.setTextColor(ThemeHelper.getForegroundColor());
                            }

                            rootView.addView(toolbar, 0);
                            return false;
                        }
                );
            }
        }
    }
}
