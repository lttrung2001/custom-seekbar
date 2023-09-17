package vn.trunglt.customseekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_HOVER_EXIT
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View

class SeekBar : View {
    private val circlePaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.WHITE
        }
    }

    private val seekPaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = context.getColor(R.color.light_blue_400)
        }
    }

    private val textPaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.BLACK
            textSize = customTextSize
        }
    }

    private val rect by lazy { Rect() }

    private var eventX: Float = 0F
    var value: Float = 0F
        set(value) {
            field = value
            valueString = value.toInt().toString()
            eventX = if (value > 0) {
                (value / maxValue + 1) * measuredWidth / 2
            } else if (value < 0) {
                (-value / minValue + 1) * measuredWidth / 2
            } else {
                measuredWidth / 2F
            }
            invalidate()
            seekListener?.onSeek(calculateValue())
        }
    private var valueString: String = ""
    private var minValue: Float = 0F
    private var maxValue: Float = 0F
    private var circleRadius: Float = 0F
    private var circleMargin: Float = 0F
    private var heightSize: Float = 0F
    private var customTextSize: Float = 0F

    private var seekListener: SeekListener? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, heightSize.toInt())
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            getLeftRectX(),
            0F,
            getRightRectX(),
            heightSize,
            1000F,
            1000F,
            seekPaint
        )
        canvas.drawCircle(eventX, circleRadius + circleMargin, circleRadius, circlePaint)
        textPaint.getTextBounds(valueString, 0, valueString.length, rect)
        canvas.drawText(
            valueString,
            eventX - rect.width() / 2,
            circleRadius + circleMargin + rect.height() / 2,
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.x > measuredWidth - circleRadius - circleMargin ||
            event.x < 0F + circleRadius + circleMargin) {
            // Do nothing
        } else if (event.action == ACTION_DOWN) {
            animate()
                .scaleX(1.3F)
                .scaleY(1.3F)
                .start()
        } else if (event.action == ACTION_UP) {
            animate()
                .scaleX(1.0F)
                .scaleY(1.0F)
                .start()
        } else if (event.action == ACTION_MOVE) {
            eventX = event.x
            calculateValue().also {
                seekListener?.onSeek(it)
                valueString = it.toInt().toString()
            }
            invalidate()
        }
        return true
    }

    fun setListener(seekListener: SeekListener) {
        this.seekListener = seekListener
    }

    private fun getLeftRectX(): Float {
        return if (eventX <= measuredWidth / 2) {
            eventX - circleRadius - circleMargin
        } else {
            eventX + circleRadius + circleMargin
        }
    }

    private fun getRightRectX(): Float {
        return if (eventX <= measuredWidth / 2) {
            measuredWidth / 2 + circleRadius + circleMargin
        } else {
            measuredWidth / 2 - circleRadius - circleMargin
        }
    }

    private fun calculateValue(): Float {
        return if (eventX > measuredWidth / 2) {
            maxValue * (- measuredWidth / 2 + eventX + circleRadius + circleMargin) / (measuredWidth / 2)
        } else if (eventX < measuredWidth / 2) {
            minValue * (measuredWidth / 2 - eventX + circleRadius + circleMargin) / (measuredWidth / 2)
        } else {
            0F
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyle, 0)
        minValue = a.getFloat(R.styleable.SeekBar_minValue, -100F)
        maxValue = a.getFloat(R.styleable.SeekBar_maxValue, 100F)
        circleRadius = a.getDimension(R.styleable.SeekBar_circleRadius, 0F)
        circleMargin = a.getDimension(R.styleable.SeekBar_circleMargin, 0F)
        customTextSize = a.getDimension(R.styleable.SeekBar_customTextSize, 0F)
        heightSize = (circleMargin + circleRadius) * 2

        a.recycle()
    }

    interface SeekListener {
        fun onSeek(value: Float)
    }
}