package com.example.swipefunction

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class SliderDoor : ConstraintLayout{
    private var track: Drawable? = null
    private var background: View? = null
    private var listener: OnUnlockListener? = null
    private var seekbar: SeekBar? = null
    private var label: TextView? = null
    private var thumbWidth = 0

    interface OnUnlockListener {
        fun onUnlock()
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun setOnUnlockListener(listener: OnUnlockListener?) {
        this.listener = listener
    }

    fun reset() {
        seekbar?.progress = 0
    }

    @SuppressLint( "ClickableViewAccessibility", "CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) { return }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.slider_door_item, this, true)

        label = findViewById<View>(R.id.slider_label) as TextView
        seekbar = findViewById<View>(R.id.slider_seekbar) as SeekBar
        background = findViewById(R.id.slider_bg)

        val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Slider)
        val text = attributes.getString(R.styleable.Slider_text)
        val thumb = attributes.getDrawable(R.styleable.Slider_thumb)
        track = attributes.getDrawable(R.styleable.Slider_trackPoint)
        attributes.recycle()
        thumbWidth = thumb?.intrinsicWidth ?: 0
//        if (track != null) {
//            background?.background = track
//        }
        if (text != null) {
            label?.text = text
        }

        val defaultOffset = seekbar?.thumbOffset
        seekbar?.thumb = thumb
        seekbar?.thumbOffset = defaultOffset ?: 0
        seekbar?.setOnTouchListener(object : OnTouchListener {
            private var isInvalidMove = false
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isInvalidMove = motionEvent.x > thumbWidth
                        return isInvalidMove
                    }

                    MotionEvent.ACTION_MOVE -> return isInvalidMove
                    MotionEvent.ACTION_UP -> return isInvalidMove
                }
                return false
            }
        })

        seekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromTouch: Boolean) {
                label?.alpha = 1f - progress * 0.02f
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (seekBar.progress < 90) {
                    val anim = ObjectAnimator.ofInt(seekBar, "progress", 0)
                    anim.interpolator = AccelerateDecelerateInterpolator()
                    anim.duration = 100.toLong()
                    anim.start()
                } else {
                    if (listener != null) {
                        listener?.onUnlock()
                        seekBar.progress = 0
                    }
                }
            }

        })
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isInEditMode) {
            return
        }
    //prevents 9-patch background image from full size stretching
//        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            background?.layoutParams?.height = seekbar?.height?.plus(fromDpToPx(3))
//        }
    }

//    private fun fromDpToPx(dp: Int): Int {
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
//    }
}