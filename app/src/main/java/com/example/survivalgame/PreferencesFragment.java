package com.example.survivalgame;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        ListPreference playerColorPreference = findPreference("player_color");
        ListPreference enemyColorPreference = findPreference("enemy_color");

        // show toast whenever a new color is chosen
        if (playerColorPreference != null) {
            playerColorPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = playerColorPreference.findIndexOfValue(newValue.toString());

                if (index != -1) {
                    CharSequence[] entries = playerColorPreference.getEntries();
                    showToastChosenColor(entries[index]);

                    return true;
                }
                return false;
            });
        }

        // show toast whenever a new color is chosen
        if (enemyColorPreference != null) {
            enemyColorPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = enemyColorPreference.findIndexOfValue(newValue.toString());

                if (index != -1) {
                    CharSequence[] entries = enemyColorPreference.getEntries();
                    showToastChosenColor(entries[index]);

                    return true;
                }
                return false;
            });
        }
    }

    private void showToastChosenColor(CharSequence chosenColor) {
        Toast.makeText(getActivity(), getString(R.string.toast_chosen_color, chosenColor), Toast.LENGTH_SHORT).show();
    }


}
