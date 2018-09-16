package br.ufpe.cin.if710.rss

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.itemlista.view.*

class AdapterRSS (private val listRSS: List<ItemRSS>, private val context: Context): RecyclerView.Adapter<AdapterRSS.ViewHolderRSS>() {

    class ViewHolderRSS(itemView: View): RecyclerView.ViewHolder(itemView) {
        // utilizado para referenciar os campos do layout, para assim poderem ser alterados em onBindViewHolder
        val titulo = itemView.item_titulo
        val data = itemView.item_data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRSS {
        // utilizado para inflar o layout do itemlista para cada item da lista de RSSs
        val view = LayoutInflater.from(context).inflate(R.layout.itemlista, parent, false)
        return ViewHolderRSS(view)
    }

    override fun getItemCount(): Int {
        return listRSS.size
    }

    override fun onBindViewHolder(holder: ViewHolderRSS, position: Int) {
        val rss = listRSS[position]
        holder.titulo.text = rss.title // coloca o titulo do RSS no layout
        holder.data.text = rss.pubDate // coloca a data do RSS no layou
        holder.titulo.setOnClickListener {
            // utilizado para abrir o RSS no navegador
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(rss.link))
            context.startActivity(i)
        }
    }
}