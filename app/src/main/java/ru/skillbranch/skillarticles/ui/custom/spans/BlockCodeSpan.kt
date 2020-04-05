package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.data.repositories.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    val rectf = RectF()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        when(type) {
            Element.BlockCode.Type.SINGLE -> {
                paint.forBackground {
                    rectf.set(0f, top + padding, canvas.width.toFloat(), bottom - padding)
                    canvas.drawRoundRect(rectf, cornerRadius, cornerRadius, paint)
                }

                paint.forText {
                    canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
                }
            }
            Element.BlockCode.Type.START -> {
                paint.forBackground {
                    path.reset()
                    rectf.set(0f, top + padding, canvas.width.toFloat(), bottom.toFloat())
                    path.addRoundRect(
                        rectf,
                        floatArrayOf(
                            cornerRadius, cornerRadius, // Top left radius in px
                            cornerRadius, cornerRadius, // Top right radius in px
                            0f, 0f, // Bottom right radius in px
                            0f, 0f // Bottom left radius in px
                        ),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }

                paint.forText {
                    canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
                }
            }
            Element.BlockCode.Type.MIDDLE -> {
                paint.forBackground {
                    rectf.set(0f, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rectf, paint)
                }

                paint.forText {
                    canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
                }
            }
            Element.BlockCode.Type.END -> {
                paint.forBackground {
                    rectf.set(0f, top.toFloat(), canvas.width.toFloat(), bottom - padding)
                    paint.forBackground {
                        path.reset()
                        rectf.set(0f, top.toFloat(), canvas.width.toFloat(), bottom - padding)
                        path.addRoundRect(
                            rectf,
                            floatArrayOf(
                                0f, 0f,
                                0f, 0f,
                                cornerRadius, cornerRadius,
                                cornerRadius, cornerRadius
                            ),
                            Path.Direction.CW
                        )
                        canvas.drawPath(path, paint)
                    }
                }

                paint.forText {
                    canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
                }
            }

        }
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm != null) {
            val originalAscent = paint.ascent()
            val originalDescent = paint.descent()
            when (type) {
                Element.BlockCode.Type.START -> {
                    fm.ascent = (originalAscent - 2 * padding).toInt()
                    fm.descent = originalDescent.toInt()
                }
                Element.BlockCode.Type.END -> {
                    fm.ascent = originalAscent.toInt()
                    fm.descent = (originalDescent + 2 * padding).toInt()
                }
                Element.BlockCode.Type.MIDDLE -> {
                    fm.ascent = originalAscent.toInt()
                    fm.descent = originalDescent.toInt()
                }
                Element.BlockCode.Type.SINGLE -> {
                    fm.ascent = (originalAscent - 2 * padding).toInt()
                    fm.descent = (originalDescent + 2 * padding).toInt()
                }
            }
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return 0
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize = textSize
        val oldStyle = typeface?.style ?: 0
        val oldFont = typeface
        val oldColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)
        textSize *= 0.85f

        block()

        textSize = oldSize
        typeface = oldFont
        color = oldColor

    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldStyle = style
        val oldColor = color

        color = bgColor
        style = Paint.Style.FILL

        block()

        style = oldStyle
        color = oldColor
    }
}
