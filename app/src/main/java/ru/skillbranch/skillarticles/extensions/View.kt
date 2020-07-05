package ru.skillbranch.skillarticles.extensions

import android.view.View
import androidx.core.view.*
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    right: Int = paddingRight,
    top: Int = paddingTop,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    val item = menu.findItem(destination.id) ?: menu.children.last()
    item.isChecked = true
}

fun BottomNavigationView.selectItem(itemId: Int?){
    itemId?: return
    for (item in menu.iterator()) {
        if(item.itemId == itemId) {
            item.isChecked = true
            break
        }
    }
}