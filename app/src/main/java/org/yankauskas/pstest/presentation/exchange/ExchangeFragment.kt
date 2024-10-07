package org.yankauskas.pstest.presentation.exchange

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.yankauskas.pstest.R
import org.yankauskas.pstest.databinding.FragmentExchangeBinding
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.ExchangeError
import org.yankauskas.pstest.domain.model.ExchangeResult
import org.yankauskas.pstest.domain.model.Transaction
import java.math.BigDecimal

class ExchangeFragment : Fragment() {
    private val viewModel: ExchangeViewModel by viewModel()
    private lateinit var binding: FragmentExchangeBinding

    private val walletsAdapter = WalletAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExchangeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()
        setListeners()
        observeWallets()
        observePickers()
        observeOperation()
        observeExchangeResult()
    }

    private fun initRV() {
        binding.rvWallets.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        binding.rvWallets.adapter = walletsAdapter
    }

    private fun setListeners() {
        binding.pickerSell.setOnClickListener { showPicker { viewModel.pickFromCurrency(it) } }
        binding.pickerReceive.setOnClickListener { showPicker { viewModel.pickToCurrency(it) } }
        binding.btnExchange.setOnClickListener { viewModel.performOperation() }

        binding.valueSell.doAfterTextChanged {
            it.toString().takeIf { it.isNotEmpty() }?.let {
                it.toBigDecimalOrNull()?.let {
                    viewModel.watchOperation(it)
                }
            } ?: viewModel.watchOperation(BigDecimal.ZERO)
        }
    }

    private fun observeWallets() {
        lifecycleScope.launch {
            viewModel.walletsFlow.collectLatest { walletsAdapter.setItems(it) }
        }
    }

    private fun observePickers() {
        viewModel.fromCurrency.observe(viewLifecycleOwner) { binding.pickerSell.text = getString(R.string.picker_value, it.code) }
        viewModel.toCurrency.observe(viewLifecycleOwner) { binding.pickerReceive.text = getString(R.string.picker_value, it.code) }
    }

    private fun observeOperation() {
        viewModel.operationFlow.observe(viewLifecycleOwner) { binding.valueReceive.text = it.toPlainString() }
    }

    private fun observeExchangeResult() {
        lifecycleScope.launch {
            viewModel.performOperationEvent.collectLatest {
                when (it) {
                    is ExchangeResult.Success -> processSuccess(it.transaction)
                    is ExchangeResult.Error -> processError(it.error)
                }
            }
        }
    }

    private fun showPicker(onItemSelected: (Currency) -> Unit) {
        val items = viewModel.walletsFlow.value.keys.toList()

        AlertDialog.Builder(requireContext())
            .setItems(items.map { it.code }.toTypedArray()) { dialog, which ->
                onItemSelected(items[which])
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun processSuccess(transaction: Transaction) = with(transaction) {
        if (fee <= BigDecimal.ZERO) {
            Toast.makeText(
                requireContext(),
                getString(R.string.success_message_f, toAmount, toCurrency.code, fromAmount, fromCurrency.code),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(), getString(
                    R.string.success_message_fee_f, toAmount, toCurrency.code, fromAmount, fromCurrency.code, fee, fromCurrency.code
                ), Toast.LENGTH_SHORT
            ).show()
        }
        binding.valueSell.text = null
    }

    private fun processError(exchangeError: ExchangeError) = with(exchangeError) {
        when (this) {
            is ExchangeError.RateExpired -> {
                Toast.makeText(requireContext(), R.string.error_rate_expired, Toast.LENGTH_SHORT).show()
            }

            is ExchangeError.SameCurrency -> {
                Toast.makeText(requireContext(), R.string.error_same_currency, Toast.LENGTH_SHORT).show()
            }

            is ExchangeError.NoRate -> {
                Toast.makeText(
                    requireContext(), getString(
                        R.string.error_no_rate_f,
                        fromCurrency.code,
                        toCurrency.code
                    ), Toast.LENGTH_SHORT
                ).show()
            }

            is ExchangeError.InsufficientFunds -> {
                Toast.makeText(
                    requireContext(), getString(
                        R.string.error_not_enough_balance_f,
                        fromCurrency.code,
                        amount
                    ), Toast.LENGTH_SHORT
                ).show()
            }

            is ExchangeError.NoSuchWallet -> {
                Toast.makeText(
                    requireContext(), getString(
                        R.string.error_no_such_wallet,
                        currency.code
                    ), Toast.LENGTH_SHORT
                ).show()
            }

            is ExchangeError.ZeroAmount -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_zero_amount, currency.code),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ExchangeError.Unknown -> {
                Toast.makeText(
                    requireContext(),
                    message.takeIf { it.isNotEmpty() } ?: getString(R.string.error_message_undefined),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}