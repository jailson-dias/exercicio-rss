package br.ufpe.cin.if710.rss

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class DownloadService : IntentService("DownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        if (PermissionChecker.checkSelfPermission(applicationContext, Manifest.permission.INTERNET) == PermissionChecker.PERMISSION_GRANTED) {
            download(intent?.getStringExtra("feed"), intent?.getStringExtra("from"))
        } else {
            Log.e("prepi errro service", "Não foi possível fazer o download")
        }
    }

    private fun download(feed: String?, from: String?) {

        // utilizado para fazer a requisição do RSS, pois não pode ser feito na thread principal
        var `in`: InputStream? = null
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            `in` = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int = `in`.read(buffer)
            while (count != -1) {
                out.write(buffer, 0, count)
                count = `in`.read(buffer)
            }
            val response = out.toByteArray()
            val listRSS = ParserRSS.parse(String(response)) // convertendo de xml para lista de itemRSS
            val db = SQLiteRSSHelper.getInstance(this)
            val tam = db.getAllItemRSS().size
            listRSS.forEach {
                db.insertItem(it).toString()
            }
            if (tam != db.getAllItemRSS().size && from == null) {
                // chamando broadcast de notificacao quando existe novos itens no banco
                val i = Intent(DOWNLOAD_AND_SAVE_COMPLETED)
                i.setClass(applicationContext, BackgroundReceiver::class.java)
                sendBroadcast(i)
            }
        } catch (e: IOException) {
            println (e.printStackTrace())
        } finally {
            `in`?.close()
        }
    }

    companion object {

        val DOWNLOAD_AND_SAVE_COMPLETED = "br.ufpe.cin.if710.rss.android.action.broadcast"
    }
}
