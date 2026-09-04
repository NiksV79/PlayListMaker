package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val ET_ID = "ET_ID"
        const val ET_DEF = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<View>(R.id.lsr_back)
        btnBack.setOnClickListener {
            finish()
        }

        val et = findViewById<EditText>(R.id.lsr_edittext)

        val tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {twOnTextChanged(et,s)}
            override fun afterTextChanged(s: Editable?) {}
        }

        et.setOnTouchListener { v, event -> etOnTouch(v as EditText, event) }
        et.addTextChangedListener(tw)

        twOnTextChanged(et,et.text)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val et = findViewById<EditText>(R.id.lsr_edittext)
        outState.putString(ET_ID,et.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val etText = savedInstanceState.getString(ET_ID,ET_DEF)
        val et = findViewById<EditText>(R.id.lsr_edittext)
        et.setText(etText)
        twOnTextChanged(et,et.text)
    }

    private fun twOnTextChanged(et: EditText, s: CharSequence?) {
        val startDrawable = getDrawable(R.drawable.ic_search14)
        val endDrawable = if (s.isNullOrEmpty()) null else getDrawable(R.drawable.ic_clear)
        et.setCompoundDrawablesWithIntrinsicBounds(startDrawable,null,endDrawable,null)
    }

    private fun isTouchInDrawableEnd(et: EditText, event: MotionEvent): Boolean {
        val drawableEnd = et.compoundDrawables[2] ?: return false
        val iconX = et.right - et.compoundPaddingEnd - drawableEnd.intrinsicWidth
        return event.x >= iconX
    }

    private fun etOnTouch(et: EditText, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (isTouchInDrawableEnd(et, event)) {
                    et.setText("")
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et.windowToken,0)
                    return true
                } else return false
            }
            else -> return false
        }
    }

}
