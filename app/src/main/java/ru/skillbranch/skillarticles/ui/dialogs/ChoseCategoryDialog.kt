package ru.skillbranch.skillarticles.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_category_dialog.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

/**
 * A simple [Fragment] subclass.
 */
class ChoseCategoryDialog : DialogFragment() {
    companion object {
        const val CHOOSE_CATEGORY_KEY = "CHOOSE_CATEGORY_KEY"
        const val SELECTED_CATEGORIES = "SELECTED_CATEGORIES"
    }

    private val viewModel: ArticlesViewModel by activityViewModels()
    private val selected = mutableListOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()

    private val categoryAdapter = CategoryAdapter { categoryId: String, isChecked: Boolean ->
        if (isChecked) selected.add(categoryId)
        else selected.remove(categoryId)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selected.clear()
        selected.addAll(
            savedInstanceState?.getStringArray("checked") ?: args.selectedCategories
        )

        val categoryItems =
            args.categories.map { it.toItem(selected.contains(it.categoryId)) }

        categoryAdapter.submitList(categoryItems)

        val listView =
            layoutInflater.inflate(R.layout.fragment_choose_category_dialog, null) as RecyclerView

        with(listView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Chose category")
            .setPositiveButton("Apply") { _, _ ->
                setFragmentResult(CHOOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to selected.toList()))
            }
            .setNegativeButton("Reset") { _, _ ->
                setFragmentResult(CHOOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to emptyList<String>()))
            }
            .setView(listView)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selected.toTypedArray())
        super.onSaveInstanceState(outState)
    }

}

class CategoryVH(override val containerView: View, val listener: (String, Boolean) -> Unit) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(item: CategoryDataItem) {
        ch_select.setOnCheckedChangeListener(null)
        ch_select.isChecked = item.isChecked
        Glide.with(containerView.context)
            .load(item.icon)
            .apply(RequestOptions.circleCropTransform())
            .override(iv_icon.width)
            .into(iv_icon)
        tv_category.text = item.title
        tv_count.text = "${item.articlesCount}"

        ch_select.setOnCheckedChangeListener { _, checked -> listener(item.categoryId, checked) }
        itemView.setOnClickListener { ch_select.toggle() }
    }

}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDataItem>() {
    override fun areItemsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean =
        oldItem == newItem
}

data class CategoryDataItem(
    val categoryId: String,
    val icon: String,
    val title: String,
    val articlesCount: Int = 0,
    val isChecked: Boolean = false
)

fun CategoryData.toItem(checked: Boolean = false) =
    CategoryDataItem(categoryId, icon, title, articlesCount, checked)