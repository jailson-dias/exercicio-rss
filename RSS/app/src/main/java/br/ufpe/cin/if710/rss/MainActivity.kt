package br.ufpe.cin.if710.rss

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity: Activity() {

    //ao fazer envio da resolucao, use este link no seu codigo!
//    private val RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml"

    //OUTROS LINKS PARA TESTAR...
    // esses outros links não funcionam com o adapter
//    private val RSS_FEED = "http://rss.cnn.com/rss/edition.rss"
//    private val RSS_FEED = "http://pox.globo.com/rss/g1/brasil/"
//    private val RSS_FEED = "http://pox.globo.com/rss/g1/ciencia-e-saude/"
//    private val RSS_FEED = "http://pox.globo.com/rss/g1/tecnologia/"

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
//    private var conteudoRSS: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        conteudoRSS.apply {
            // colocando para o recycle view utilizar o layout do linearlayoutmanager
            layoutManager = LinearLayoutManager(applicationContext)

        }
    }

    override fun onStart() {
        super.onStart()
        // pegando a url que está no arquivo strings.xml e depois requisitando-a com getRssFeed
        getRssFeed(getString(R.string.rssfeed))

    }

    //Opcional - pesquise outros meios de obter arquivos da internet - bibliotecas, etc.
    private fun getRssFeed(feed: String) {

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
}
