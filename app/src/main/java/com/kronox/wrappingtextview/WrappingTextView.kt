package com.kronox.wrappingtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.AppCompatTextView
import android.text.Layout
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout


/**
 * Custom TextView inherited from [AppCompatTextView] for displaying wrapping text with image prefixed on TextView.
 *
 * This layout allows the text to wrap around a View created in the same container ([RelativeLayout]). The View will be placed on the left side of the spanning text.
 * The View which is to be wrapped inside the text will be adjusted to have the height of a single line in the TextView.
 * This is made possible by utilizing the SpannableString in Android. The text will start from right side of the View.
 * And if it pursues to next line, the remaining text will be displayed underneath the View.
 *
 * @author Mathew Jacob
 */
class WrappingTextView : AppCompatTextView {

    private val TAG: String = "WrappingTextView"
    private var mTextView: AppCompatTextView? = null
    private var mView: View? = null
    private val SPACE_AFTER_FLASH_ICON = 0
    var numberOfLines: Int = 5
    var wrapAroundId: Int? = -1
    var isWrapped: Boolean? = false

    //For getting the width needed for icon, a dummy content is created and loaded into the TextView
    private val dummyString: String = "dummy string"

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WrappingTextView)
        this.wrapAroundId = ta.getResourceId(R.styleable.WrappingTextView_wrapAroundId, -1)
        this.isWrapped = ta.getBoolean(R.styleable.WrappingTextView_isWrapped, false)
        this.numberOfLines = ta.getInteger(R.styleable.WrappingTextView_numberOfLines, 1)
        ta.recycle()
    }

    /**
     * Sets the text to be displayed. It will wrap the text on the right side of the provided View.
     *
     * @param text text to be displayed
     */
    fun setText(textContent: String) {
        this.mView = (parent as ViewGroup).findViewById(this.wrapAroundId!!)
        this.mTextView = this

        if (isWrapped!!) {
            mView!!.visibility = View.VISIBLE

            val textPaint = mTextView!!.paint
            val textRect = Rect()
            textPaint.getTextBounds(dummyString, 0, dummyString.length, textRect)
            val textFontPadding = mTextView!!.paint.fontMetrics.bottom.toInt()
            val textPaddingTop = mTextView!!.paddingTop + textFontPadding
            val textPaddingLeft = mTextView!!.paddingLeft
            val textLineHeight = textRect.height() - textFontPadding

            val backgroundDrawable = mView!!.background
            if (backgroundDrawable != null) {
                val intrinsicWidth = backgroundDrawable.intrinsicWidth
                val intrinsicHeight = backgroundDrawable.intrinsicHeight
                val aspectRatio = intrinsicWidth.toFloat() / intrinsicHeight
                val textLeftMargin = ((textLineHeight * numberOfLines) * aspectRatio).toInt()

                val imageParams = mView!!.layoutParams as RelativeLayout.LayoutParams
                imageParams.setMargins(textPaddingLeft, textPaddingTop + 5, 0, 0)
                imageParams.height = textLineHeight * numberOfLines
                imageParams.width = textLeftMargin
                mView!!.layoutParams = imageParams

                val spannableString = SpannableString(textContent)
                //Expose the indent for the specified rows
                val marginSpan2 = LeadingMarginSpan2Helper(
                        numberOfLines, textLeftMargin + SPACE_AFTER_FLASH_ICON)
                spannableString.setSpan(marginSpan2, 0, spannableString.length, 0)

                mTextView!!.text = spannableString
            } else {
                Log.e(TAG, "Error loading background of View to be wrapped inside..!!")
            }
        } else {
            mView!!.visibility = View.GONE
            mTextView!!.text = textContent
        }
    }

    private inner class LeadingMarginSpan2Helper internal constructor(private val lines: Int, private val margin: Int) : LeadingMarginSpan.LeadingMarginSpan2 {

        /*Returns the value to which must be added indentation*/
        override fun getLeadingMargin(first: Boolean): Int {
            return if (first) {
                /*This * indentation is applied to the number of          rows returned * getLeadingMarginLineCount ()*/
                margin
            } else {
                //Offset for all other Layout layout ) { }
                /*Returns * the number of rows which should be applied *     indent returned by getLeadingMargin (true)
                 * Note:* Indent only applies to N lines of the first paragraph.*/
                0
            }
        }

        override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                       top: Int, baseline: Int, bottom: Int, text: CharSequence,
                                       start: Int, end: Int, first: Boolean, layout: Layout) {
        }

        override fun getLeadingMarginLineCount(): Int {
            return lines
        }
    }
}
