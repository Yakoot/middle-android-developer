package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import kotlin.math.abs
import kotlin.math.max

@SuppressLint("ViewConstructor")
class ArticleItemView (context: Context) : ViewGroup(context, null, 0) {

    //views
    val tv_date: TextView
    val tv_author: TextView
    val tv_title: TextView
    val tv_description: TextView
    val tv_likes_count: TextView
    val tv_comments_count: TextView
    val tv_read_duration: TextView
    val iv_poster: ImageView
    val iv_category: ImageView
    val iv_likes: ImageView
    val iv_comments: ImageView
    val iv_bookmark: ImageView

    //colors
    @ColorInt
    private val colorGray: Int = context.getColor(R.color.color_gray)
    @ColorInt
    private val colorPrimary: Int = context.getColor(R.color.color_primary)

    //sizes
    private val basePadding = context.dpToIntPx(16)
    private val posterSize = context.dpToIntPx(64)
    private val categorySize = context.dpToIntPx(40)
    private val imagesSize = posterSize + categorySize / 2
    private val bottomIconSize = context.dpToIntPx(16)
    private val cornerRadius = context.dpToIntPx(8)
    private val baseMargin = context.dpToIntPx(8)


    private val baseFontSize = 12f
    private val descriptionFontSize = 14f
    private val titleFontSize = 18f

    init {
        setPadding(basePadding)

        tv_date = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_date
        }
        addView(tv_date)

        tv_author = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorPrimary)
            id = R.id.tv_author
        }
        addView(tv_author)

        tv_title = TextView(context).apply {
            textSize = titleFontSize
            setTextColor(colorPrimary)
            setTypeface(typeface, Typeface.BOLD)
            id = R.id.tv_title
        }
        addView(tv_title)

        iv_poster = ImageView(context).apply {
            layoutParams = LayoutParams(posterSize, posterSize)
            id = R.id.iv_poster
        }
        addView(iv_poster)

        iv_category = ImageView(context).apply {
            layoutParams = LayoutParams(categorySize, categorySize)
            id = R.id.iv_category
        }
        addView(iv_category)

        tv_description = TextView(context).apply {
            textSize = descriptionFontSize
            setTextColor(colorGray)
            id = R.id.tv_description
        }
        addView(tv_description)

        iv_likes = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_likes
        }
        addView(iv_likes)

        tv_likes_count = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_likes_count
        }
        addView(tv_likes_count)

        iv_comments = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_comments
        }
        addView(iv_comments)

        tv_comments_count = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_comments_count
        }
        addView(tv_comments_count)

        tv_read_duration = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_read_duration
        }
        addView(tv_read_duration)


        iv_bookmark = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_bookmark
        }
        addView(iv_bookmark)
    }

    fun bind(item: ArticleItemData) {
        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_date.text = item.date.format()
        tv_author.text = item.author
        tv_title.text = item.title
        tv_description.text = item.description
        tv_likes_count.text = "${item.likeCount}"
        tv_comments_count.text = "${item.commentCount}"
        tv_read_duration.text = "${item.readDuration} min read"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        // first line
        measureChild(tv_date, widthMeasureSpec, heightMeasureSpec)
        tv_author.maxWidth = width - tv_date.width - basePadding * 3
        measureChild(tv_author, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tv_date.height
        usedHeight += baseMargin

        // 2nd line
        tv_title.maxWidth = width - posterSize - categorySize / 2 - basePadding * 2 - baseMargin / 2
        measureChild(tv_title, widthMeasureSpec, heightMeasureSpec)


        usedHeight += max(tv_title.measuredHeight, imagesSize)





        usedHeight += paddingBottom
        setMeasuredDimension(width, usedHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        var currentLeftPosition = paddingLeft
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        // first line (author + date)
        tv_date.layout(
            currentLeftPosition,
            usedHeight,
            currentLeftPosition + tv_date.measuredWidth,
            usedHeight + tv_date.measuredHeight
        )
        currentLeftPosition += tv_date.measuredWidth + basePadding

        tv_author.layout(
            currentLeftPosition,
            usedHeight,
            currentLeftPosition + tv_author.measuredWidth,
            usedHeight + tv_author.measuredHeight
        )

        usedHeight += tv_author.measuredHeight
        usedHeight += baseMargin
        currentLeftPosition = paddingLeft

        // 2nd line
        val sizeDiff = abs(imagesSize - tv_title.measuredHeight)
        if (imagesSize > tv_title.measuredHeight) {
            tv_title.layout(
                currentLeftPosition,
                usedHeight + sizeDiff / 2,
                currentLeftPosition + tv_title.measuredWidth,
                usedHeight + tv_title.measuredHeight + sizeDiff / 2
            )
            iv_poster.layout(
                right - posterSize,
                usedHeight,
                right,
                usedHeight + imagesSize - categorySize / 2
            )
            iv_category.layout(
                right - imagesSize,
                usedHeight + posterSize - categorySize / 2,
                right - posterSize + categorySize / 2,
                usedHeight + imagesSize
            )

        } else {
            tv_title.layout(
                currentLeftPosition,
                usedHeight + sizeDiff / 2,
                currentLeftPosition + tv_title.measuredWidth,
                usedHeight + tv_title.measuredHeight + sizeDiff / 2
            )
            iv_poster.layout(
                right - posterSize,
                usedHeight,
                right,
                usedHeight + imagesSize - categorySize / 2
            )
            iv_category.layout(
                right - imagesSize,
                usedHeight - posterSize - categorySize / 2,
                right - posterSize + categorySize / 2,
                usedHeight + imagesSize
            )
        }

        usedHeight += baseMargin



    }
}