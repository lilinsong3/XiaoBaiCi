package com.github.lilinsong3.xiaobaici.ui.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.lilinsong3.xiaobaici.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String NIGHT_MODE_LIST_PREFERENCE_KEY = "nightMode";
    public static final String ABOUT_APP_PREFERENCE_KEY = "aboutApp";
    public static final String FEEDBACK_PREFERENCE_KEY = "feedback";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        setupNightModeListPreference();
        setupAboutAppPreference();
        setupFeedbackPreference();
    }

    private void setupNightModeListPreference() {
        ListPreference nightModeListPreference = findPreference(NIGHT_MODE_LIST_PREFERENCE_KEY);
        if (nightModeListPreference != null) {
            nightModeListPreference.setOnPreferenceChangeListener( (preference, newValue) -> {
                AppCompatDelegate.setDefaultNightMode(Integer.parseInt((String) newValue));
                return true;
            });
        }
    }

    private void setupAboutAppPreference() {
        Preference aboutAppPreference = findPreference(ABOUT_APP_PREFERENCE_KEY);
        if (aboutAppPreference != null) {
            aboutAppPreference.setOnPreferenceClickListener(preference -> {
                NavHostFragment.findNavController(this).navigate(SettingsFragmentDirections.actionSettingsToAboutApp());
                return true;
            });
        }
    }

    private void setupFeedbackPreference() {
        Preference feedbackPreference = findPreference(FEEDBACK_PREFERENCE_KEY);
        if (feedbackPreference != null) {
            feedbackPreference.setOnPreferenceClickListener(preference -> new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.short_feedback)
                    .setMessage(R.string.long_feedback_instruction)
                    .setNegativeButton(R.string.short_cancel, (dialog, which) -> dialog.cancel())
                    // 复制邮件地址到剪贴板
                    .setNeutralButton(R.string.short_copy_email, (dialog, which) -> {
                        ClipboardManager clipboardManager = (ClipboardManager)requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.short_email_address), getString(R.string.long_my_email)));
                        dialog.dismiss();
                        Toast.makeText(requireContext(), getString(R.string.long_copy_email_notice), Toast.LENGTH_SHORT).show();
                    }).show().isShowing());
        }
    }
}