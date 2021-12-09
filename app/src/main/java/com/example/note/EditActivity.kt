package com.example.note

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.example.note.EditActivity
import com.example.note.translate.TranslateActivity
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : BaseActivity() {
    private var editText //编辑的笔记
            : EditText? = null
    private var old_content: String? = ""
    private var old_time: String? = ""
    private var old_tag = 1
    private var id: Long = 0
    private var openMode = 0
    private val tag = 1
    private val tagChange = false
    private var noteToolbar: Toolbar? = null

    /**
     * 引入menu菜单栏
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout)
        editText = findViewById(R.id.editText1)
        noteToolbar = findViewById(R.id.noteToolbar)
        editText!!.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.add(0, 100, 0, "Note文本翻译")
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                if (item.itemId == 100) {
                    val sta = editText!!.getSelectionStart()
                    val end = editText!!.getSelectionEnd()
                    val words = editText!!.getText().toString().substring(sta, end)
                    val intent = Intent(this@EditActivity, TranslateActivity::class.java)
                    intent.putExtra("translate_words", words)
                    startActivity(intent)
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
        }

        //设置Action Bar，自定义Toolbar
        setSupportActionBar(noteToolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //设置toolber取代action bar
        //设置Navigation返回键的事件监听，与系统的返回键功能相同
        noteToolbar?.setNavigationOnClickListener(View.OnClickListener {
            autoSetMessage(intent) //自动更新笔记
            setResult(RESULT_OK, intent)
            finish()
        })
        val getIntent = getIntent()
        //定义意图
        openMode = getIntent.getIntExtra("mode", 0)
        Log.d("System=====", "openMode:$openMode")
        if (openMode == 3) {
            //打开已存在的note，将内容写入到已编辑的笔记中，实现继续编辑
            id = getIntent.getLongExtra("id", 0)
            old_content = getIntent.getStringExtra("content")
            old_time = getIntent.getStringExtra("time")
            old_tag = getIntent.getIntExtra("tag", 1)
            editText!!.setText(old_content) //填充内容
            editText!!.setSelection(old_content!!.length) //移动光标的位置（最后），方便再次书写
        }
    }

    /**
     * 设置菜单栏按钮监听
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent()
        when (item.itemId) {
            R.id.delete -> AlertDialog.Builder(this@EditActivity)
                .setMessage("您确定要删除吗？")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    if (openMode == 4) { //如果是新增笔记，则不创建
                        intent.putExtra("mode", -1)
                        setResult(RESULT_OK, intent)
                    } else { //如果是修改笔记，则删除
                        intent.putExtra("mode", 2)
                        intent.putExtra("id", id)
                        setResult(RESULT_OK, intent)
                    }
                    finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    dialog.dismiss() //关闭窗口
                }.create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 系统按钮监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent()
            autoSetMessage(intent)
            //按下返回键，将页面的文本获取，并返回
//            intent.putExtra("content", editText.getText().toString());
//            intent.putExtra("time", dateToStr());
            setResult(RESULT_OK, intent)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 判断是新增笔记还是修改笔记,mode值是3为修改，为4是新增
     */
    fun autoSetMessage(intent: Intent) {
        if (openMode == 4) {
            Log.d("input", "input11111")
            //判断笔记是否为空，若为空，则不新增笔记
            if (editText!!.text.toString().length == 0) {
                intent.putExtra("mode", -1)
            } else {
                intent.putExtra("mode", 0)
                intent.putExtra("content", editText!!.text.toString())
                intent.putExtra("time", dateToStr())
                intent.putExtra("tag", tag)
            }
        } else {
            //判断笔记是否被修改，或者标签是否更换，否则不更新笔记
            if (editText!!.text.toString() == old_content && !tagChange) {
                intent.putExtra("mode", -1)
            } else {
                intent.putExtra("mode", 1)
                intent.putExtra("content", editText!!.text.toString())
                intent.putExtra("time", dateToStr())
                intent.putExtra("id", id)
                intent.putExtra("tag", tag)
            }
        }
    }

    /**
     * 转换时间格式
     *
     * @return
     */
    fun dateToStr(): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(date)
    }
}