package com.github.lilinsong3.xiaobaici.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.github.lilinsong3.xiaobaici.R

class GridTextView : View {

    /**
     * 默认值
     */
    private companion object {
        const val DEFAULT_TEXT_SIZE: Float = 32f
        const val DEFAULT_CELL_GAP: Int = 16
        const val DEFAULT_TEXT_COLOR: Int = Color.BLACK
        const val DEFAULT_BORDER_COLOR: Int = Color.BLACK
        const val DEFAULT_BORDER_THICKNESS: Float = 4f
        const val DEFAULT_DASH_COLOR: Int = Color.GRAY
        const val DEFAULT_DASH_THICKNESS: Float = 5f
        const val DEFAULT_DASH_LENGTH: Float = 8f
        const val DEFAULT_DASH_GAP: Float = 4f
    }

    /**
     * 文本
     */
    private var _text: String? = null
        set(value) {
            field = value
            contentDescription = value
        }

    var text: String?
        get() = _text
        set(value) {
            _text = value
            invalidateTextPaintAndMeasurements()
            // requestLayout()
        }

    /**
     * 字的数量
     */
    private val numOfWords: Int
        get() = _text?.length ?: 0

    /**
     * 文本颜色
     */
    private var _textColor: Int = DEFAULT_TEXT_COLOR
    var textColor: Int
        get() = _textColor
        set(value) {
            _textColor = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * 文本尺寸
     */
    private var _textSize: Float = DEFAULT_TEXT_SIZE
    var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
            invalidateTextPaintAndMeasurements()
        }

    enum class TextPosition {
        START_TOP,
        CENTER_TOP,
        END_TOP,
        START_CENTER,
        CENTER,
        END_CENTER,
        START_BOTTOM,
        CENTER_BOTTOM,
        END_BOTTOM;

        companion object {
            @JvmStatic
            fun getValue(ordinal: Int): TextPosition {
                if (ordinal < 0) {
                    return START_TOP
                }
                val values = TextPosition.values()
                if (ordinal < values.size) {
                    return values[ordinal]
                }
                return START_TOP
            }
        }
    }

    /**
     * 文本的位置
     */
    var textPosition: TextPosition = TextPosition.START_TOP

    /**
     * 边框颜色
     */
    private var _borderColor: Int = DEFAULT_BORDER_COLOR
    var borderColor: Int
        get() = _borderColor
        set(value) {
            _borderColor = value
            invalidateBorderPaintAndMeasurements()
        }

    /**
     * 边框宽度
     */
    private var _borderThickness: Float = DEFAULT_BORDER_THICKNESS
    var borderThickness: Float
        get() = _borderThickness
        set(value) {
            _borderThickness = value
            invalidateBorderPaintAndMeasurements()
        }

    /**
     * 虚线颜色
     */
    private var _dashColor: Int = DEFAULT_DASH_COLOR
    var dashColor: Int
        get() = _dashColor
        set(value) {
            _dashColor = value
            invalidateDashPaintAndMeasurements()
        }

    /**
     * 虚线宽度
     */
    private var _dashThickness: Float = DEFAULT_DASH_THICKNESS
    var dashThickness: Float
        get() = _dashThickness
        set(value) {
            _dashThickness = value
            invalidateDashPaintAndMeasurements()
        }

    /**
     * 虚线线段长度
     */
    private var _dashLength: Float = DEFAULT_DASH_LENGTH
    var dashLength: Float
        get() = _dashLength
        set(value) {
            _dashLength = value
            invalidateDashPaintAndMeasurements()
        }

    /**
     * 虚线线段间隔
     */
    private var _dashGap: Float = DEFAULT_DASH_GAP
    var dashGap:Float
        get() = _dashGap
        set(value) {
            _dashGap = value
            invalidateDashPaintAndMeasurements()
        }

    /**
     * 文本宽度
     */
    private var textWidth: Float = 0f

    /**
     * 文本高度
     */
    private var textHeight: Float = 0f

    /**
     * 行与行之间的间隔
     */
    var rowGap: Int = DEFAULT_CELL_GAP

    /**
     * 列与列之间的间隔
     */
    var colGap: Int = DEFAULT_CELL_GAP

    /**
     * 格子边长
     */
    private var cellSideLength: Int = 0

    /**
     * 文本边界矩形
     */
    private val textBoundsRect: Rect = Rect()

    /**
     * 格子矩形
     */
    private val cellRect: Rect = Rect()

    /**
     * 边界画笔
     */
    private val borderPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    /**
     * 文本画笔
     */
    private val textPaint: TextPaint = TextPaint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        textAlign = Paint.Align.LEFT
    }

    /**
     * 虚线画笔
     */
    private val dashPaint: Paint = Paint().apply {
        // 抗锯齿
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    /**
     * 每个字的宽度
     */
    private lateinit var wordWidths: FloatArray


    constructor(context: Context) : super(context) {
        init(null, R.attr.GridTextViewDefault, R.style.GridTextView)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, R.attr.GridTextViewDefault, R.style.GridTextView)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, R.attr.GridTextViewDefault, R.style.GridTextView)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.GridTextView, defStyle, defStyleRes
        )

        _text = a.getString(R.styleable.GridTextView_text)
        _textColor = a.getColor(R.styleable.GridTextView_textColor, textColor)
        _textSize = a.getDimension(R.styleable.GridTextView_textSize, textSize)

        textPosition = TextPosition.getValue(a.getInt(R.styleable.GridTextView_textPosition, textPosition.ordinal))

        colGap = a.getDimensionPixelSize(R.styleable.GridTextView_colGap, colGap)
        rowGap = a.getDimensionPixelSize(R.styleable.GridTextView_rowGap, rowGap)

        _borderColor = a.getColor(R.styleable.GridTextView_borderColor, borderColor)
        _borderThickness = a.getDimension(R.styleable.GridTextView_borderThickness, borderThickness)

        _dashColor = a.getColor(R.styleable.GridTextView_dashColor, dashColor)
        _dashThickness = a.getDimension(R.styleable.GridTextView_dashThickness, dashThickness)
        _dashLength = a.getDimension(R.styleable.GridTextView_dashLength, dashLength)
        _dashGap = a.getDimension(R.styleable.GridTextView_dashGap, dashGap)

        a.recycle()

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
        // Update
        invalidateBorderPaintAndMeasurements()
        invalidateDashPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            val nonNullText = text ?: ""

            it.textSize = textSize
            it.color = textColor

            textWidth = it.measureText(nonNullText)
            val fm = it.fontMetrics
            textHeight = fm.bottom - fm.top

            it.getTextBounds(nonNullText, 0, numOfWords, textBoundsRect)

            wordWidths = FloatArray(numOfWords)
            it.getTextWidths(nonNullText, wordWidths)
        }
    }

    private fun invalidateBorderPaintAndMeasurements() {
        dashPaint.run {
            color = dashColor
            strokeWidth = dashThickness
            pathEffect = DashPathEffect(floatArrayOf(dashLength, dashGap), 0f)
        }
    }

    private fun invalidateDashPaintAndMeasurements() {
        borderPaint.run {
            color = borderColor
            strokeWidth = borderThickness
        }
    }

    private fun evaluateSuitableSize(givenSize: Int, gap: Int): Int {
        if (cellSideLength == 0) return 0
        val cellWithGap = cellSideLength + gap

        // 格子加间隔的数量
        val num = givenSize / cellWithGap
        if (num < 1) return 0

        // 剩余空间
        val remainderSize = givenSize % cellWithGap
        // 如果剩余空间可以容纳一个格子，则减去最后用不了的空间
        // 否则减去多出来的一个间隔和剩余空间
        val unusedPiece = remainderSize - cellSideLength
        return if (unusedPiece < 0) {
            givenSize - gap - remainderSize
        } else {
            givenSize - unusedPiece
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (numOfWords < 1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // 列与列的间隔总和
        val colGaps = colGap * (numOfWords - 1)
        // 所有格子总宽度
        val widthOfAllCells = (paddingStart + paddingEnd) * numOfWords + textBoundsRect.width()
        // 单格高度
        val cellHeight = paddingTop + paddingBottom + textHeight
        // 单格宽度
        val cellWidth = widthOfAllCells / numOfWords

        // 比较宽高，取长的那边作格子的边长
        cellSideLength = cellWidth.coerceAtLeast(cellHeight.toInt())
        // 取了边长后，计算单行宽度
        val desireOnlyOneRowWidth = cellSideLength * numOfWords  + colGaps

        // 测量宽度
        val minMeasuredWidth: Int
        val wSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSpecSize = MeasureSpec.getSize(widthMeasureSpec)

        minMeasuredWidth = when (wSpecMode) {
            MeasureSpec.EXACTLY -> wSpecSize
            MeasureSpec.UNSPECIFIED -> desireOnlyOneRowWidth
            MeasureSpec.AT_MOST -> {
                if (wSpecSize < desireOnlyOneRowWidth) {
                    evaluateSuitableSize(wSpecSize, colGap)
                } else {
                    desireOnlyOneRowWidth
                }
            }
            else -> 0
        }

        // 测量高度
        val minMeasuredHeight: Int

        // 一行格子的数量
        val cellsNumInRow = if (minMeasuredWidth == 0) 0 else minMeasuredWidth / (cellSideLength + colGap) + 1

        if (cellsNumInRow == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        } else {
            // 计算机正整数相除 向上取整公式 x/y = (x + y - 1) / y
            // 列包含的格子数量
            val cellsNumInCol = (numOfWords + cellsNumInRow - 1) / cellsNumInRow
            // 高度
            val desireHeight = cellSideLength * cellsNumInCol + rowGap * (cellsNumInCol - 1)

            val hSpecMode = MeasureSpec.getMode(heightMeasureSpec)
            val hSpecSize = MeasureSpec.getSize(heightMeasureSpec)

            minMeasuredHeight = when (hSpecMode) {
                MeasureSpec.EXACTLY -> hSpecSize
                MeasureSpec.UNSPECIFIED -> desireHeight
                MeasureSpec.AT_MOST -> if (hSpecSize < desireHeight) evaluateSuitableSize(hSpecSize, rowGap) else desireHeight
                else -> 0
            }
        }

        setMeasuredDimension(minMeasuredWidth, minMeasuredHeight)
    }

    /**
     * 确定画布原点位置
     */
    private fun translateStartPosition(canvas: Canvas, usedWidth: Int, usedHeight: Int) {
        when (textPosition) {
            TextPosition.START_TOP -> canvas.translate(
                0f,
                0f
            )
            TextPosition.CENTER_TOP -> canvas.translate(
                (width - usedWidth) / 2f,
                0f
            )
            TextPosition.END_TOP -> canvas.translate(
                (width - usedWidth).toFloat(),
                0f
            )
            TextPosition.START_CENTER -> canvas.translate(
                0f,
                (height -usedHeight) / 2f
            )
            TextPosition.CENTER -> canvas.translate(
                (width - usedWidth) / 2f,
                (height -usedHeight) / 2f
            )
            TextPosition.END_CENTER -> canvas.translate(
                (width - usedWidth).toFloat(),
                (height -usedHeight) / 2f
            )
            TextPosition.START_BOTTOM -> canvas.translate(
                0f,
                (height - usedHeight).toFloat()
            )
            TextPosition.CENTER_BOTTOM -> canvas.translate(
                (width - usedWidth) / 2f,
                (height - usedHeight).toFloat()
            )
            TextPosition.END_BOTTOM -> canvas.translate(
                (width - usedWidth).toFloat(),
                (height - usedHeight).toFloat()
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        // 一行可容纳的格子数量
        var cellsInRow = width / (cellSideLength + colGap)
        // 剩余宽度
        val remainderWidth = width % (cellSideLength + colGap)
        if (remainderWidth >= cellSideLength) {
            cellsInRow += 1
        }

        if (cellsInRow == 0) {
            super.onDraw(canvas)
            return
        }

        // 计算机正整数相除 向上取整公式 x/y = (x + y - 1) / y
        // 列可容纳的格子数量
        val cellsInCol = (numOfWords + cellsInRow - 1) / cellsInRow

        // 确定画布原点
        translateStartPosition(
            canvas,
            cellsInRow.coerceAtMost(numOfWords) * (cellSideLength + colGap) - colGap,
            cellsInCol * (cellSideLength + rowGap) - rowGap
        )

        colLoop@ for (i in 0 until cellsInCol) {
            for (j in 0 until cellsInRow) {
                // 字索引
                val wordIndex = i * cellsInRow + j
                if (wordIndex >= numOfWords) {
                    break@colLoop
                }

                // 边框
                val left = j * (cellSideLength + colGap)
                val top = i * (cellSideLength + rowGap)
                val right = left + cellSideLength
                val bottom = top + cellSideLength
                cellRect.set(left, top, right, bottom)
                canvas.drawRect(cellRect, borderPaint)

                // 十字虚线
                // 横虚线
                val horizontalDashStartX = left.toFloat()
                val horizontalDashStartY = top + cellSideLength / 2f
                val horizontalDashStopX = horizontalDashStartX + cellSideLength
                canvas.drawLine(
                    horizontalDashStartX,
                    horizontalDashStartY,
                    horizontalDashStopX,
                    horizontalDashStartY,
                    dashPaint
                )
                // 竖虚线
                val verticalDashStartX = left + cellSideLength / 2f
                val verticalDashStartY = top.toFloat()
                val verticalDashStopY = verticalDashStartY + cellSideLength
                canvas.drawLine(
                    verticalDashStartX,
                    verticalDashStartY,
                    verticalDashStartX,
                    verticalDashStopY,
                    dashPaint
                )

                // 字
                val x = left + (cellSideLength - wordWidths[wordIndex]) / 2f
                // 基线坐标 = top + 单个田字格高度的一半 + （由文字top和bottom计算得到的文字高度的一半 - 文字的bottom）
                val y = top + cellSideLength / 2f + (textHeight / 2f - textPaint.fontMetrics.bottom)
                canvas.drawText(
                    text ?: "",
                    wordIndex,
                    wordIndex + 1,
                    x,
                    y,
                    textPaint
                )
            }
        }
    }
}