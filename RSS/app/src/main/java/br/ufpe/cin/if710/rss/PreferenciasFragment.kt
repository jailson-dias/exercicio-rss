package br.ufpe.cin.if710.rss


import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log

class PreferenciasFragment() : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i("prepi", key)
        Log.i("prepi", sharedPreferences?.getString(key, "nao deu certo"))
//        Log.i("prepi", preferenceManager.sharedPreferences?.getString(key, "deu nao"))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?,
                                     rootKey: String?) {
        setPreferencesFromResource(R.xml.preferencias, rootKey)

        val shared = preferenceManager.sharedPreferences
        shared.registerOnSharedPreferenceChangeListener(this)
        preferenceManager.sharedPreferencesName = "rssfeed"
//        this.onSharedPreferenceChanged(shared, "rssFeed")
    }

}
