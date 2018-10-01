package br.ufpe.cin.if710.rss

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatCallback
import android.support.v7.app.AppCompatDelegate
import android.support.v7.view.ActionMode
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity: AppCompatActivity(), AppCompatCallback {

    val receiver = ForegroundReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // colocando a toolbar no app
        setSupportActionBar(toolbar)

        // registrando o broadcast para ser chamando quando o app estiver em primeiro plano
        registerReceiver(receiver, IntentFilter(FOREGROUND))
//        registerReceiver(BackgroundReceiver(), IntentFilter(DownloadService.DOWNLOAD_AND_SAVE_COMPLETED))

        conteudoRSS.apply {
            // colocando para o recycle view utilizar o layout do linearlayoutmanager
            layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    // criando o menu com a opção de settings
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // colocando a ação de ir para a tela de configuração quando clicar em settings
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.btn_Config -> {
                val i = Intent(this, PreferenciasActivity::class.java)
                startActivity(i)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }


    override fun onStart() {
        super.onStart()
        // pegando a url que está no arquivo strings.xml e depois requisitando-a com getRssFeed
//        var rssfeed = getSharedPreferences("rssFeed", Context.MODE_PRIVATE)

        sendBroadcast(Intent(FOREGROUND))
        getRssFromDB()
    }

    private fun getRssFromDB() {
        // lê as noticias do banco local
        doAsync {
            val db = SQLiteRSSHelper.getInstance(applicationContext)
            val listRSS = db.getAllItemRSS()
            uiThread {
                Log.i("prepi rss", listRSS.size.toString())
                conteudoRSS.adapter = AdapterRSS(listRSS, applicationContext) // criando o adapter para exibir a lista de rss
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // verifica se tem novas noticias ao sair
        val sharedPreferences = getSharedPreferences("rssfeed", Context.MODE_PRIVATE)
        val url = sharedPreferences!!.getString("rssFeed", "")

        if (url!!.endsWith(".xml", true)) {

            // solicitando o service para baixar os dados do xml e salvar no DB
            val i = Intent(applicationContext, DownloadService::class.java)
            i.putExtra("feed", url)
            startService(i)
        } else {
            Log.i("prepi", "A URL informada não é um xml válido")
        }
    }

    //Opcional - pesquise outros meios de obter arquivos da internet - bibliotecas, etc.
    private fun getRssFeed(feed: String?) {

        // utilizado para fazer a requisição do RSS, pois não pode ser feito na thread principal
        doAsync {
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
                uiThread {
                    val listRSS = ParserRSS.parse(String(response)) // convertendo de xml para lista de itemRSS
                    conteudoRSS.adapter = AdapterRSS(listRSS, applicationContext) // criando o adapter para exibir a lista de rss
                }
            } catch (e: IOException) {
                println (e.printStackTrace())
            } finally {
                `in`?.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        val FOREGROUND = "br.ufpe.cin.if710.rss.FOREGROUND"
    }
}
