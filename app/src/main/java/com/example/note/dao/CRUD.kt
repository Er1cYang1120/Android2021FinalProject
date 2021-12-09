package com.example.note.dao

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import com.example.note.dao.NoteDatabase
import com.example.note.dao.CRUD
import android.widget.BaseAdapter
import com.example.note.adapter.NoteAdapter.MyFilter
import android.view.ViewGroup
import android.preference.PreferenceManager
import com.example.note.R
import android.widget.TextView
import android.widget.Filter.FilterResults
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.note.BaseActivity
import android.widget.EditText
import com.example.note.translate.TranslateActivity
import android.app.Activity
import android.content.*
import android.widget.AdapterView.OnItemClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.note.adapter.NoteAdapter
import android.widget.PopupWindow
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.view.WindowManager
import android.util.DisplayMetrics
import com.example.note.EditActivity
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.widget.AdapterView
import com.example.note.entity.Note
import java.util.ArrayList

class CRUD(context: Context?) {
    var dbHandler //数据库处理器
            : SQLiteOpenHelper
    var db //定义数据库
            : SQLiteDatabase? = null

    //对数据库进行写入功能
    fun open() {
        db = dbHandler.writableDatabase
    }

    //关闭数据库
    fun close() {
        dbHandler.close()
    }

    //添加笔记,把note加入到database里面
    fun addNote(note: Note): Note {
        //判断笔记是否为空，为空则不添加
//        if(note.getContent().length()==0){
//            removeNote(note);
//            return null;
//        }
        //添加一个笔记到数据库
        //专门处理数据的一个类，相当于一个内容值
        val contentValues = ContentValues()
        contentValues.put(NoteDatabase.Companion.CONTENT, note.content)
        contentValues.put(NoteDatabase.Companion.TIME, note.time)
        contentValues.put(NoteDatabase.Companion.MODE, note.tog)
        val insertId = db!!.insert(NoteDatabase.Companion.TABLE_NAME, null, contentValues)
        note.id = insertId
        return note
    }

    //通过id获取Note数据
    fun getNote(id: Long): Note {
        val cursor = db!!.query(
            NoteDatabase.Companion.TABLE_NAME,
            columns,
            NoteDatabase.Companion.ID + "=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor?.moveToFirst()
        return Note(cursor!!.getString(1), cursor.getString(2), cursor.getInt(3))
    }

    //通过id获取Note数据
    val allNotes: List<Note>
        get() {
            val cursor =
                db!!.query(NoteDatabase.Companion.TABLE_NAME, columns, null, null, null, null, null)
            val list: MutableList<Note> = ArrayList()
            var note: Note? = null
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    note = Note()
                    note.id = cursor.getLong(cursor.getColumnIndex(NoteDatabase.Companion.ID))
                    note.content =
                        cursor.getString(cursor.getColumnIndex(NoteDatabase.Companion.CONTENT))
                    note.time = cursor.getString(cursor.getColumnIndex(NoteDatabase.Companion.TIME))
                    note.tog = cursor.getInt(cursor.getColumnIndex(NoteDatabase.Companion.MODE))
                    list.add(note)
                }
            }
            return list
        }

    //修改笔记
    fun updateNote(note: Note): Int {
        val values = ContentValues()
        values.put(NoteDatabase.Companion.CONTENT, note.content)
        values.put(NoteDatabase.Companion.TIME, note.time)
        values.put(NoteDatabase.Companion.MODE, note.tog)
        return db!!.update(
            NoteDatabase.Companion.TABLE_NAME, values,
            NoteDatabase.Companion.ID + "=?", arrayOf(note.id.toString())
        )
    }

    //删除笔记
    fun removeNote(note: Note) {
        db!!.delete(
            NoteDatabase.Companion.TABLE_NAME,
            NoteDatabase.Companion.ID + "=" + note.id,
            null
        )
    }

    companion object {
        //取出数据库的数据
        private val columns = arrayOf<String>(
            NoteDatabase.Companion.ID,
            NoteDatabase.Companion.CONTENT,
            NoteDatabase.Companion.TIME,
            NoteDatabase.Companion.MODE
        )
    }

    //构造方法
    init {
        //实例化SQLiteOpenHelper
        dbHandler = NoteDatabase(context)
    }
}