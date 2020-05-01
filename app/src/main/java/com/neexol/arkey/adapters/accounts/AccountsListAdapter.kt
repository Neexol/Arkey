package com.neexol.arkey.adapters.accounts

import android.animation.ValueAnimator
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var collapsedHeight = 0
    private var expandedHeight = 0

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AccountHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_account, parent, false)
        )

    override fun onBindViewHolder(holder: AccountHolder, position: Int) = holder.bind(position)

    inner class AccountHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
        private val cardView: CardView = view.cardView
        private val accountName: TextView = view.accountName

        init {
            view.accountName.setOnClickListener {
//                clickListener?.onAccountClick(dataList[adapterPosition])
                toggleCardViewHeight(expandedHeight)
            }
            cardView.viewTreeObserver
                .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        cardView.viewTreeObserver.removeOnPreDrawListener(this)
                        collapsedHeight = cardView.accountName.height
                        expandedHeight = cardView.height
                        val layoutParams: ViewGroup.LayoutParams = cardView.layoutParams
                        layoutParams.height = collapsedHeight
                        cardView.layoutParams = layoutParams
                        return true
                    }
                })
        }

        private fun toggleCardViewHeight(height: Int) {
            if (cardView.height == collapsedHeight) {
                expandView(height)
            } else {
                collapseView()
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

        fun bind(position: Int) {
            accountName.text = dataList[position].name
        }
    }

    fun setOnAccountClickListener(clickListener: OnAccountsListClickListener) {
        this.clickListener = clickListener
    }

    interface OnAccountsListClickListener {
        fun onAccountClick(account: Account)
    }
}