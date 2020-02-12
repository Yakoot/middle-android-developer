package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.layout_bottombar.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {
    var isSearchmode = false

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return BottombarBehavior()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.ssIsSearchMode = isSearchmode
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isSearchmode = state.ssIsSearchMode
            reveal.isVisible = !isSearchmode
            group_bottom.isVisible = !isSearchmode
        }
    }

    private class SavedState: BaseSavedState, Parcelable {
        var ssIsSearchMode: Boolean = false

        constructor(superState: Parcelable?): super(superState)

        constructor(src: Parcel): super(src) {
            ssIsSearchMode = src.readInt() == 1
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (ssIsSearchMode) 1 else 0)
        }

        override fun describeContents() = 0

        companion object CREATOR: Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel) = SavedState(source)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    init {
        View.inflate(context, R.layout.layout_bottombar, this)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }
}