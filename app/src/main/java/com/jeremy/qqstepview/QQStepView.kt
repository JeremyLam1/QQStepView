package com.jeremy.qqstepview


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * 自定义仿QQ计步器View
 * Created by Jeremy on 2017/11/22.
 */

class QQStepView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    /**
     * 文本与弧的间隔
     */
    private var txt2ArcOffet = 50

    /**
     * 默认圆弧大小
     */
    private var defaultArcSize = 300

    /**
     * 进度圆弧颜色
     */
    private var mInnerColor = Color.RED

    /**
     * 外圆弧颜色
     */
    private var mOuterColor = Color.BLUE

    /**
     * 圆弧的宽度(dp)
     */
    private var mBorderWidth = 10.0.toFloat()

    /**
     * 当前步数
     */
    private var mCurrStep = 0

    /**
     * 最大步数
     */
    private var mMaxStep = 0

    /**
     * 内容文本大小(sp)
     */
    private var mTextSize = 20.0.toFloat()

    /**
     * 内容文本颜色
     */
    private var mTextColor = Color.RED

    /**
     * 是否动画开始显示
     */
    private var mStartWithAnim = true

    /**
     * 圆弧的画笔
     */
    private var mArcPaint: Paint? = null

    /**
     * 圆弧边界
     */
    private var mArcRect: RectF? = null

    /**
     * 文字的画笔
     */
    private var mTextPaint: TextPaint? = null

    var innerColor: Int
        get() = mInnerColor
        set(innerColor) {
            mInnerColor = innerColor
        }

    var outerColor: Int
        get() = mOuterColor
        set(outerColor) {
            mOuterColor = outerColor
        }

    var borderWidth: Float
        get() = mBorderWidth
        set(borderWidth) {
            mBorderWidth = borderWidth
        }

    var currStep: Int
        get() = mCurrStep
        @Synchronized
        set(currStep) {
            mCurrStep = currStep
            invalidate()
        }

    var maxStep: Int
        get() = mMaxStep
        @Synchronized
        set(maxStep) {
            mMaxStep = maxStep
        }

    var textSize: Float
        get() = mTextSize
        set(textSize) {
            mTextSize = textSize
        }

    var textColor: Int
        get() = mTextColor
        set(textColor) {
            mTextColor = textColor
        }

    init {
        init(context, attrs!!, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray = context.obtainStyledAttributes(
                attrs, R.styleable.QQStepView, defStyleAttr, 0)
        mInnerColor = typeArray.getColor(R.styleable.QQStepView_innerColor, mInnerColor)
        mOuterColor = typeArray.getColor(R.styleable.QQStepView_outerColor, mOuterColor)
        mBorderWidth = typeArray.getDimension(R.styleable.QQStepView_borderWidth, dp2px(mBorderWidth))
        mCurrStep = typeArray.getInt(R.styleable.QQStepView_currStep, mCurrStep)
        mMaxStep = typeArray.getInt(R.styleable.QQStepView_maxStep, mMaxStep)
        mTextSize = typeArray.getDimension(R.styleable.QQStepView_stepTextSize, mTextSize)
        mTextColor = typeArray.getColor(R.styleable.QQStepView_stepTextColor, mTextColor)
        mStartWithAnim = typeArray.getBoolean(R.styleable.QQStepView_startWithAnim, mStartWithAnim)

        typeArray.recycle()

        mArcPaint = Paint()
        mArcPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mArcPaint!!.strokeCap = Paint.Cap.ROUND
        mArcPaint!!.style = Paint.Style.STROKE
        mArcPaint!!.strokeWidth = mBorderWidth

        mTextPaint = TextPaint()
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.textSize = sp2px(mTextSize)
        mTextPaint!!.color = mTextColor

        mArcRect = RectF()

        txt2ArcOffet = dp2px(txt2ArcOffet.toFloat()).toInt()
        defaultArcSize = dp2px(defaultArcSize.toFloat()).toInt()

        if (mStartWithAnim) {
            setupAndStartAnim()
        }
    }

    /**
     * 执行动画
     */
    private fun setupAndStartAnim() {
        val angleAnim = ValueAnimator()
        angleAnim.setIntValues(0, mCurrStep)
        angleAnim.duration = 1500
        angleAnim.startDelay = 200
        angleAnim.interpolator = AccelerateDecelerateInterpolator()
        angleAnim.addUpdateListener {
            val currStep = angleAnim.animatedValue
            this.currStep = currStep as Int
        }
        angleAnim.start()
    }

    /**
     * dp转px
     */
    private fun dp2px(textSize: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize, context.resources.displayMetrics)

    /**
     * sp转px
     */
    private fun sp2px(textSize: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.resources.displayMetrics)

    /**
     * 起始角度
     */
    private val ANGLE_BEGIN = 135f

    /**
     * 结束角度
     */
    private val ANGLE_END = 270f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paddingOffset = mBorderWidth / 2
        mArcRect!!.left = paddingLeft.toFloat() + paddingOffset
        mArcRect!!.right = width - paddingLeft - paddingOffset
        mArcRect!!.top = paddingTop.toFloat() + paddingOffset
        mArcRect!!.bottom = height - paddingBottom - paddingOffset

        //画外圆弧
        mArcPaint!!.color = mOuterColor
        canvas!!.drawArc(mArcRect, ANGLE_BEGIN, ANGLE_END, false, mArcPaint)

        if (mMaxStep == 0) return

        //画进度圆弧
        mArcPaint!!.color = mInnerColor
        if (mCurrStep > mMaxStep) {
            mCurrStep = mMaxStep
        }
        val sweepAngle = mCurrStep / mMaxStep.toFloat()
        canvas.drawArc(mArcRect, ANGLE_BEGIN, sweepAngle * ANGLE_END, false, mArcPaint)

        var msg = mCurrStep.toString()
        val fmi = mTextPaint!!.fontMetricsInt
        val maxTextWidth = width - 2 * mBorderWidth - 2 * txt2ArcOffet
        var textWidth = mTextPaint!!.measureText(msg)
        //当前设置的宽度小于字体需要的宽度，将字体改为xxx...
        if (textWidth > maxTextWidth) {
            msg = TextUtils.ellipsize(msg, mTextPaint, maxTextWidth, TextUtils.TruncateAt.END).toString()
            textWidth = maxTextWidth
        }
        val textX = (width - textWidth) / 2
        //计算基线
        val dy = (fmi.bottom - fmi.top) / 2 - fmi.bottom
        val textY = height / 2 + dy
        //画文本
        canvas.drawText(msg, textX, textY.toFloat(), mTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var mWidth = widthSize
        var mHeight = heightSize


        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = paddingLeft + defaultArcSize + paddingRight
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = paddingTop + defaultArcSize + paddingBottom
        }

        val minSize = if (mWidth > mHeight) mHeight else mWidth
        setMeasuredDimension(minSize, minSize)
    }

}
