package com.neexol.arkey.adapters.accounts

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.text.method.PasswordTransformationMethod
import android.view.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.databinding.ItemAccountBinding
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.addToClipboard
import com.neexol.arkey.utils.toast
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var clickListener: OnAccountsListClickListener? = null

    private var dataList = listOf<Account>()

    fun updateDataList(newDataList: List<Account>) {
        GlobalScope.launch(Dispatchers.Main) {
            val accountsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    AccountsListDiffUtilCallback(
                        dataList,
                        newDataList
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            dataList = newDataList
            accountsDiffResult.dispatchUpdatesTo(this@AccountsListAdapter)
        }
    }

    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val binding = DataBindingUtil.inflate<ItemAccountBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_account,
            parent,
            false
        )
        return AccountHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) = holder.bind(position)

    inner class AccountHolder internal constructor(private val binding: ItemAccountBinding):
        RecyclerView.ViewHolder(binding.root) {
        private var collapsedHeight = 0
        private var expandedHeight = 0

        private val cardView: CardView = binding.cardView

        init {
            binding.editAccountBtn.setOnClickListener {
                clickListener?.onAccountEditClick(dataList[adapterPosition])
            }
            binding.collapsedPanel.setOnClickListener {
                toggleCardViewHeight(expandedHeight)
            }
            binding.copyLoginBtn.setOnClickListener {
                it.context.addToClipboard("login", dataList[adapterPosition].login)
                it.toast(it.context.getString(R.string.copied_clipboard))
            }
            binding.copyPasswordBtn.setOnClickListener {
                it.context.addToClipboard("password", dataList[adapterPosition].password)
                it.toast(it.context.getString(R.string.copied_clipboard))
            }
            binding.visibilityPasswordBtn.setOnClickListener {
                if (it.isActivated) {
                    binding.passwordHolder.transformationMethod = PasswordTransformationMethod()
                } else {
                    binding.passwordHolder.transformationMethod = null
                }
                it.isActivated = !it.isActivated
            }
            cardView.viewTreeObserver
                .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        cardView.viewTreeObserver.removeOnPreDrawListener(this)
                        cardView.layoutParams = cardView.layoutParams.apply {
                            height = cardView.accountName.height
                        }
                        return true
                    }
                })
        }

        fun bind(position: Int) {
            val account = dataList[position]

            if (account.login.isEmpty() &&
                account.password.isEmpty() &&
                account.site.isEmpty() &&
                account.description.isEmpty()
            ) {
                binding.notFilledNotification.visibility = View.VISIBLE
            } else {
                binding.notFilledNotification.visibility = View.GONE
            }

            binding.account = account
            binding.executePendingBindings()

            cardView.viewTreeObserver
                .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        cardView.viewTreeObserver.removeOnPreDrawListener(this)
                        collapsedHeight = cardView.accountName.height
                        expandedHeight = cardView.height
                        cardView.layoutParams = cardView.layoutParams.apply {
                            height = collapsedHeight
                        }
                        return true
                    }
                })
        }

        private fun toggleCardViewHeight(height: Int) {
            if (cardView.height == collapsedHeight) {
                expandView(height)
                binding.editAccountBtn.visibility = View.VISIBLE
            } else {
                collapseView()
                binding.editAccountBtn.visibility = View.GONE
            }
        }

        private fun collapseView() {
            val anim = ValueAnimator.ofInt(
                cardView.measuredHeightAndState,
                collapsedHeight
            )
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = cardView.layoutParams
                layoutParams.height = value
                cardView.layoutParams = layoutParams
            }
            anim.start()
        }

        private fun expandView(height: Int) {
            val anim = ValueAnimator.ofInt(
                cardView.measuredHeightAndState,
                height
            )
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = cardView.layoutParams
                layoutParams.height = value
                cardView.layoutParams = layoutParams
            }
            anim.start()
        }
    }

    fun setOnAccountClickListener(clickListener: OnAccountsListClickListener) {
        this.clickListener = clickListener
    }

    interface OnAccountsListClickListener {
        fun onAccountEditClick(account: Account)
    }
}