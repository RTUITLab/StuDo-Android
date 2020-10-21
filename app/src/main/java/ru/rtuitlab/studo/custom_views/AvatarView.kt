@file:Suppress("MemberVisibilityCanBePrivate")

package ru.rtuitlab.studo.custom_views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import ru.rtuitlab.studo.R
import java.util.*
import kotlin.math.min


class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isAvatarExist = false

    var text = ""

    var avatarBackgroundColor = 0
        set(value){
        field = value
        paintBackground.color = field
        }

    var avatarLetterColor = 0
        set(value){
            field = value
            paintLetter.color = field
        }

    private val paintBackground = Paint()
    private val paintLetter = Paint(Paint.LINEAR_TEXT_FLAG)

    private var circleX = 0f
    private var circleY = 0f
    private var circleRadius = 0f

    private var textX = 0f
    private var textY = 0f


    init {
        attrs?.let{
            val array = context.obtainStyledAttributes(it, R.styleable.AvatarView)

            text = array.getString(
                R.styleable.AvatarView_avatarText
            )?.trim()?.toUpperCase(Locale.ROOT) ?: "--"

            avatarBackgroundColor = array.getColor(
                R.styleable.AvatarView_avatarBackgroundColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            avatarLetterColor = array.getColor(
                R.styleable.AvatarView_avatarTextColor,
                Color.WHITE
            )

            array.recycle()
        }
        init()
    }

    private fun init(){
        paintBackground.style = Paint.Style.FILL
        paintBackground.color = avatarBackgroundColor
        paintBackground.isAntiAlias = true

        paintLetter.textAlign = Align.CENTER
        paintLetter.isAntiAlias = true
        paintLetter.style = Paint.Style.FILL
        paintLetter.color = avatarLetterColor

        drawable?.let{ setAvatar(it) }
    }

    fun setAvatar(drawable: Drawable){
        val bitmap = getBitmapFromDrawable(drawable)
        bitmap?.let{
            setImageBitmap(getCircleBitmap(bitmap))
        } ?:run {
            throw RuntimeException("Error of casting drawable to bitmap")
        }
        isAvatarExist = true
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            drawable.bitmap?.let { return drawable.bitmap }
        }

        return if (drawable.intrinsicWidth != -1 && drawable.intrinsicHeight != -1){
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            null
        }
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        canvas.drawCircle(
            bitmap.width / 2f, bitmap.height / 2f,
            bitmap.width / 2f, paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 32f, context.resources.displayMetrics
        )
        val desiredHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 32f, context.resources.displayMetrics
        )

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width : Int
        val height : Int

        if (widthMode == MeasureSpec.EXACTLY
            && heightMode == MeasureSpec.EXACTLY
            && widthSize == heightSize){
            width = widthSize
            height = heightSize
        } else if (widthMode == MeasureSpec.EXACTLY
            && heightMode == MeasureSpec.EXACTLY
            && widthSize != heightSize){
            width = min(widthSize, heightSize)
            height = min(widthSize, heightSize)
        } else {
            width = desiredWidth.toInt()
            height = desiredHeight.toInt()
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isAvatarExist){
            circleX = width / 2f
            circleY = height / 2f
            circleRadius = min(width, height) / 2f

            paintLetter.textSize = height / 2f
            textX = width / 2f
            textY = height / 2f - ((paintLetter.descent() + paintLetter.ascent()) / 2f)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (isAvatarExist){
            super.onDraw(canvas)
        } else {
            canvas?.drawCircle(circleX, circleY, circleRadius, paintBackground)
            canvas?.drawText(text, textX, textY, paintLetter)
        }
    }
}
