package ru.rtuitlab.studo.ui.general.users.fragments

import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.extensions.mainActivity

class SettingsFragment: PreferenceFragmentCompat() {

    private val recreateOnChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
        requireActivity().recreate()
        true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.settings)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)
        setListeners()
    }

    private fun setListeners() {
        findPreference<ListPreference>("themeDropdown")?.onPreferenceChangeListener = recreateOnChangeListener
        findPreference<ListPreference>("languagesDropdown")?.onPreferenceChangeListener = recreateOnChangeListener
    }
}