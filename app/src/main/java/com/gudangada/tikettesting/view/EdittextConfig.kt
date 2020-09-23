package com.gudangada.tikettesting.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.gudangada.tikettesting.Logger
import com.gudangada.tikettesting.R

class EdittextConfig : android.support.v7.widget.AppCompatEditText {

    private var mFont: String = "R"
    private var mPath: String = "fonts/sfpro-"
    private var mType: String = ".otf"

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setValues(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setFont(mFont)
        setNewTypeFace()
    }

    fun setFont(font: String) {
        mFont = font
    }

    fun getFont(): String {
        return mFont
    }

    private fun setNewTypeFace() {
        val font = Typeface.createFromAsset(context.assets, mPath + mFont + mType)
        setTypeface(font, Typeface.NORMAL)
    }

    private fun setValues(attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.textViewStyle)
        try {
            val n = attr.indexCount
            for (i in 0 until n) {
                val attribute = attr.getIndex(i)
                when (attribute) {
                    R.styleable.textViewStyle_tvStyle -> mFont = attr.getString(attribute).toString()
                    else -> Logger.d("Unknown attribute for " + javaClass.toString() + ": " + attribute)
                }
            }
        } finally {
            attr.recycle()
        }
    }

}
