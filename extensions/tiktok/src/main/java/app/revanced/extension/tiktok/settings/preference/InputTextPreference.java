package app.revanced.extension.tiktok.settings.preference;

import android.content.Context;
import android.preference.EditTextPreference;

import app.revanced.extension.shared.settings.StringSetting;

public class InputTextPreference extends EditTextPreference {

    public InputTextPreference(Context context, String title, String summary, StringSetting setting) {
        super(context);
        this.setTitle(title);
        this.setSummary(summary);
        this.setKey(setting.key);
        this.setText(setting.get());
    }
}
