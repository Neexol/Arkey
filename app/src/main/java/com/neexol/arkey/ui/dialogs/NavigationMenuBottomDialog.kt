package com.neexol.arkey.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu.NONE
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.NEW_CATEGORY_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.dialog_bottom_nav_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.Serializable

class NavigationMenuBottomDialog: BottomSheetDialogFragment() {

    companion object {
        private const val CALLBACK_KEY = "CALLBACK"

        fun newInstance(callback: OnCategoryListener): NavigationMenuBottomDialog {
            return NavigationMenuBottomDialog().apply {
                arguments = bundleOf(CALLBACK_KEY to callback)
            }
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private val categories = mutableListOf<Category>()
    private var callback: OnCategoryListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_bottom_nav_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callback = requireArguments().getSerializable(CALLBACK_KEY) as OnCategoryListener

        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
            categories.clear()
            categories.addAll(it)
            initNavigationView()
        })
    }

    private fun initNavigationView() {
        with(navigationView.menu) {
            clear()
            add(0, ALL_CATEGORIES_ID, NONE, getString(R.string.all_categories))
            add(0, WITHOUT_CATEGORY_ID, NONE, getString(R.string.without_category))
            categories.forEach {
                add(0, it.id!!, NONE, it.name)
            }
            add(0, NEW_CATEGORY_ID, NONE, getString(R.string.new_category))
        }

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                NEW_CATEGORY_ID -> callback?.onNewCategory(TODO())
                else -> viewModel.selectCategory(it.itemId)
            }
            dismiss()
            true
        }
    }

    interface OnCategoryListener: Serializable {
        fun onNewCategory(categoryName: String)
    }
}