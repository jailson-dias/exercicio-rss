package br.ufpe.cin.if710.rss

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLiteRSSHelper private constructor(//alternativa
        internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {
    val items: Cursor?
        @Throws(SQLException::class)
        get() = null

    override fun onCreate(db: SQLiteDatabase) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    fun insertItem(item: ItemRSS): Long {
        return insertItem(item.title, item.pubDate, item.description, item.link)
    }

    fun insertItem(title: String, pubDate: String, description: String, link: String): Long {
        val db = writableDatabase


        // utilizando para verificar se já tem uma nóticia com o mesmo link no banco e assim não ficar com várias noticias repetidas
        val cursor = db.rawQuery("select * from " + DATABASE_TABLE + " WHERE " + ITEM_LINK + "='" + link + "'", null)

        if (!cursor!!.moveToFirst()) {
            cursor.close()

            // montando o dado para salvar no DB
            val item = ContentValues()
            item.put(ITEM_TITLE, title)
            item.put(ITEM_DATE, pubDate)
            item.put(ITEM_DESC, description)
            item.put(ITEM_LINK, link)
            item.put(ITEM_UNREAD, true)
            return db.insert(DATABASE_TABLE,null, item)
        }
        cursor.close()

        // dado já existe no DB
        return -1
    }

    @Throws(SQLException::class)
    fun getItemRSS(link: String): ItemRSS {
        val db = writableDatabase

        // procurar item pelo link
        val cursor = db.rawQuery("select * from " + DATABASE_TABLE + " WHERE " + ITEM_LINK + "='" + link + "'", null)

        // colocando valores padrão para caso não encontre o item
        var title = "NÃO ENCONTRADO"
        var pubDate= "NÃO ENCONTRADO"
        var description = "NÃO ENCONTRADO"
        if (cursor!!.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndex(ITEM_TITLE))
            pubDate = cursor.getString(cursor.getColumnIndex(ITEM_DATE))
            description = cursor.getString(cursor.getColumnIndex(ITEM_DESC))
        }
        cursor.close()

        return ItemRSS(title, link, pubDate, description)
    }

    @Throws(SQLException::class)
    fun getAllItemRSS(): List<ItemRSS> {
        val db = writableDatabase

        // selecionando todas as noticias que não foram lidas ainda
        val cursor = db.rawQuery("select * from " + DATABASE_TABLE + " WHERE " + ITEM_UNREAD + "=1", null)

        val items = ArrayList<ItemRSS>()
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                val title= cursor.getString(cursor.getColumnIndex(ITEM_TITLE))
                val link= cursor.getString(cursor.getColumnIndex(ITEM_LINK))
                val pubDate = cursor.getString(cursor.getColumnIndex(ITEM_DATE))
                val description = cursor.getString(cursor.getColumnIndex(ITEM_DESC))
                items.add(ItemRSS(title, link, pubDate, description))
                cursor.moveToNext()
            }
        }
        cursor.close()

        return items
    }

    fun markAsUnread(link: String): Boolean {
        val db = writableDatabase
        val selection = ITEM_LINK + " LIKE ?"
        val values = ContentValues()
        values.put(ITEM_UNREAD, true) // coloca a noticia com não lida
        return db.update(DATABASE_TABLE, values, selection, arrayOf(link)) > 0
    }

    fun markAsRead(link: String): Boolean {
        val db = writableDatabase
        val selection = ITEM_LINK + " LIKE ?"
        val values = ContentValues()
        values.put(ITEM_UNREAD, false) // coloca a noticia como lida
        return db.update(DATABASE_TABLE, values, selection, arrayOf(link)) > 0
    }

    companion object {
        //Nome do Banco de Dados
        private val DATABASE_NAME = "rss"
        //Nome da tabela do Banco a ser usada
        val DATABASE_TABLE = "items"
        //Versão atual do banco
        private val DB_VERSION = 1

        @SuppressLint("StaticFieldLeak")
        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        @Synchronized
        fun getInstance(c: Context): SQLiteRSSHelper {
            if (db == null) {
                db = SQLiteRSSHelper(c.applicationContext)
            }
            return db as SQLiteRSSHelper
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = RssProviderContract._ID
        val ITEM_TITLE = RssProviderContract.TITLE
        val ITEM_DATE = RssProviderContract.DATE
        val ITEM_DESC = RssProviderContract.DESCRIPTION
        val ITEM_LINK = RssProviderContract.LINK
        val ITEM_UNREAD = RssProviderContract.UNREAD

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf<String>(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }

}
