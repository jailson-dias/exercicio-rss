package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class ForegroundReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // pegando a url por sharedPrefereces
        val sharedPreferences = context.getSharedPreferences("rssfeed", Context.MODE_PRIVATE)
        val url = sharedPreferences!!.getString("rssFeed", "")

        if (url!!.endsWith(".xml", true)) {

            // solicitando o service para baixar os dados do xml e salvar no DB
            val i = Intent(context, DownloadService::class.java)
            i.putExtra("feed", url)
            i.putExtra("from", "fore")
            context.startService(i)
        } else {
            Log.i("prepi", "A URL informada não é um xml válido")
        }
    }
}
