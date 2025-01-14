package xyz.realms.mws.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import xyz.realms.mws.MwsApp;
import xyz.realms.mws.R;
import xyz.realms.mws.corefunc.Helper;
import xyz.realms.mws.corefunc.Prefs;


public class PrefsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new PrefsFragment()
        ).commit();

    }

    public static class PrefsFragment extends PreferenceFragment {
        public static final int PREFERENCE_ACTIVITY = 0;
        public static final int PREF_RESULT_NONE = 1;
        public static final int PREF_RESULT_RESET = 2;
        private final Preference.OnPreferenceChangeListener onPasswordEnabledChange = (preference, newValue) -> {
            try {
                PrefsFragment.this.setEnableDisablePassword(preference, Boolean.parseBoolean(newValue.toString()));
                return true;
            } catch (Exception e) {
                return true;
            }
        };
        private final Preference.OnPreferenceChangeListener onHomeDirChange = (preference, newValue) -> {
            try {
                Preference homeDirPref = PrefsFragment.this.findPreference(Prefs.PREF_CUSTOMFOLDER);
                homeDirPref.setEnabled(Prefs.isHomeDirCustomDir(MwsApp.getAppContext(), Integer.parseInt(newValue.toString())));
                return true;
            } catch (Exception e) {
                return true;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            CharSequence[] mEntries;
            CharSequence[] mEntryValues;
            super.onCreate(savedInstanceState);

            ListPreference homeDirPref = (ListPreference) findPreference(Prefs.PREF_HOMEDIR);
            if (Helper.GetSecondaryPrivateDirectory(MwsApp.getAppContext()) != null) {
                mEntries = getResources().getStringArray(R.array.home_dir_api19);
                mEntryValues = getResources().getStringArray(R.array.home_dir_values_api19);
            } else {
                mEntries = getResources().getStringArray(R.array.home_dir_api8);
                mEntryValues = getResources().getStringArray(R.array.home_dir_values_api8);
            }
//            setEntries就失败。
            homeDirPref.setEntries(mEntries);
            homeDirPref.setEntryValues(mEntryValues);
            homeDirPref.setOnPreferenceChangeListener(this.onHomeDirChange);

            ListPreference lockPref = (ListPreference) findPreference(Prefs.PREF_LOCK);
            lockPref.setEntries(R.array.lock_api12);
            lockPref.setEntryValues(R.array.lock_values_api12);

            Preference customPref = findPreference(Prefs.PREF_RESETPREFS);
            customPref.setOnPreferenceClickListener(pref -> {
                Intent resultIntent = new Intent();
                if (PrefsFragment.this.getActivity() != null) {
                    PrefsFragment.this.getActivity().setResult(PREF_RESULT_RESET, resultIntent);
                    PrefsFragment.this.getActivity().finish();
                }
                return false;
            });

            setEnableDisablePassword(findPreference(Prefs.PREF_PASSWORD), Prefs.getPasswordEnabled(MwsApp.getAppContext()));
            setPreferenceChangeListener(Prefs.PREF_PASSWORD, this.onPasswordEnabledChange);
            Preference customFolderPref = findPreference(Prefs.PREF_CUSTOMFOLDER);
            customFolderPref.setOnPreferenceClickListener(pref -> {
                Intent intent = new Intent(PrefsFragment.this.getContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, Prefs.getCustomFolder(MwsApp.getAppContext()));
                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
                intent.putExtra(FileDialog.SHOW_FOLDERS_ONLY, true);
                PrefsFragment.this.startActivityForResult(intent, PREF_RESULT_NONE);
                return false;
            });
            String currentCustomFolder = Prefs.getCustomFolder(MwsApp.getAppContext());
            customFolderPref.setSummary(currentCustomFolder);
            customFolderPref.setEnabled(Prefs.isHomeDirCustomDir(MwsApp.getAppContext()));

            addPreferencesFromResource(R.xml.preference);
        }

        private void setPreferenceChangeListener(String key, Preference.OnPreferenceChangeListener listener) {
            Preference preference = findPreference(key);
            preference.setOnPreferenceChangeListener(this.onPasswordEnabledChange);
        }

        public void setEnableDisablePassword(Preference preference, boolean enabled) {
            Preference userPref = findPreference(Prefs.PREF_USERNAME);
            Preference passPref = findPreference(Prefs.PREF_USERPASS);
            userPref.setEnabled(enabled);
            passPref.setEnabled(enabled);
        }

        @Override
        public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PREF_RESULT_NONE && resultCode == -1) {
                String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                Prefs.setCustomFolder(MwsApp.getAppContext(), filePath);
                Preference customFolderPref = findPreference(Prefs.PREF_CUSTOMFOLDER);
                customFolderPref.setSummary(filePath);
            }
        }

    }
}