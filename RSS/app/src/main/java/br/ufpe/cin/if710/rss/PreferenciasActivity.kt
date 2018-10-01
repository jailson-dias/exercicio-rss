package br.ufpe.cin.if710.rss

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity

class PreferenciasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_preferencias)

        supportFragmentManager.beginTransaction().replace(android.R.id.content, PreferenciasFragment()).commit()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            PreferenceManager.setDefaultValues(applicationContext, R.xml.preferencias, false)
//        }
//        else {
//            PreferenceManager.setDefaultValues(applicationContext, R.xml.preferencias, false)
//            addPreferencesFromResource(R.xml.preferencias)
//        }

//        preferenceManager.findPreference("rssFeed")
//        SharedPreferences.OnSharedPreferenceChangeListener

    }

//    class ConfigFragment : Prefer() {
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            addPreferencesFromResource(R.xml.preferencias)
//        }
//    }
}
