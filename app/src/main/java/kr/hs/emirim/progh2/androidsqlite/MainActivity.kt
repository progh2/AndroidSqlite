package kr.hs.emirim.progh2.androidsqlite

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val dbName = "webnautes"
    private val tableName = "person"

    private val names = arrayOf(
        "Cupcake", "Donut", "Eclair", "Froyo",
        "Gingerbread", "Honeycomb",
        "Ice Cream Sandwich", "Jelly Bean", "Kitkat"
    )

    private val phones = arrayOf(
        "Android 1.5", "Android 1.6", "Android 2.0", "Android 2.2",
        "Android 2.3", "Android  3.0", "Android  4.0", "Android  4.1",
        "Android  4.4"
    )
    var personList: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()

    var list: ListView? = null
    val TAG_NAME = "name"
    val TAG_PHONE = "phone"
    var sampleDB: SQLiteDatabase? = null
    var adapter: ListAdapter? = null


    override public fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = findViewById(R.id.listView)

        try {
            var sampleDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)
            //테이블이 존재하지 않으면 새로 생성합니다.
            sampleDB.execSQL(
                "CREATE TABLE IF NOT EXISTS " + tableName
                        + " (name VARCHAR(20), phone VARCHAR(20) );"
            )
            //테이블이 존재하는 경우 기존 데이터를 지우기 위해서 사용합니다.
            sampleDB.execSQL("DELETE FROM " + tableName)
            //새로운 데이터를 테이블에 집어넣습니다..
            for (i in names.indices) {
                sampleDB.execSQL(
                    "INSERT INTO " + tableName
                            + " (name, phone)  Values ('" + names[i] + "', '" + phones[i] + "');"
                )
            }
            sampleDB.close()
        } catch (se: SQLiteException) {
            Toast.makeText(
                getApplicationContext(),
                se.message,
                Toast.LENGTH_LONG
            ).show()
            Log.e("", se.message)
        }
        showList()
    }


    private fun showList() {
        try {
            val ReadDB: SQLiteDatabase = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null)

            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            val c: Cursor? = ReadDB.rawQuery("SELECT * FROM " + tableName, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        //테이블에서 두개의 컬럼값을 가져와서
                        val Name = c.getString(c.getColumnIndex("name"))
                        val Phone = c.getString(c.getColumnIndex("phone"))

                        //HashMap에 넣습니다.
                        val persons: HashMap<String, String> = HashMap<String, String>();

                        persons.put(TAG_NAME, Name);
                        persons.put(TAG_PHONE, Phone);

                        //ArrayList에 추가합니다..
                        personList.add(persons);

                    } while (c.moveToNext());
                }
            }
            ReadDB.close();
            //새로운 apapter를 생성하여 데이터를 넣은 후..
            adapter = SimpleAdapter(
                this, personList, R.layout.list_item,
                arrayOf(TAG_NAME, TAG_PHONE),
                intArrayOf(R.id.name, R.id.phone)
            )
            //화면에 보여주기 위해 Listview에 연결합니다.
            list?.setAdapter(adapter);
        } catch (se: SQLiteException) {
            Toast.makeText(getApplicationContext(), se.message, Toast.LENGTH_LONG).show();
            Log.e("", se.message);
        }
    }
}


