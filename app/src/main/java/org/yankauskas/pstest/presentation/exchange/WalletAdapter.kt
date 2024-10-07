package org.yankauskas.pstest.presentation.exchange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.yankauskas.pstest.R
import org.yankauskas.pstest.databinding.ItemWalletBinding
import org.yankauskas.pstest.domain.model.Currency
import java.math.BigDecimal

class WalletAdapter : RecyclerView.Adapter<WalletAdapter.TextViewHolder>() {

    private val items = mutableListOf<Pair<Currency, BigDecimal>>()

    fun setItems(newItems: Map<Currency, BigDecimal>) {
        items.clear()
        items.addAll(newItems.toList())
        notifyDataSetChanged()
    }

    class TextViewHolder(private val binding: ItemWalletBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: Currency, amount: BigDecimal) {
            binding.tvWallet.text = binding.tvWallet.context.getString(R.string.balance_value_f, currency.code, amount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder =
        TextViewHolder(ItemWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(items[position].first, items[position].second)
    }

    override fun getItemCount(): Int = items.size
}