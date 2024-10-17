package org.yankauskas.pstest.presentation.compose.exchange

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.yankauskas.pstest.R
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.ExchangeError
import org.yankauskas.pstest.domain.model.ExchangeResult
import org.yankauskas.pstest.domain.model.Transaction
import org.yankauskas.pstest.presentation.exchange.ExchangeViewModel
import java.math.BigDecimal

@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel,
    modifier: Modifier = Modifier
) {
    val wallets = viewModel.walletsFlow.collectAsState(initial = emptyMap())
    val fromCurrency = viewModel.fromCurrency.observeAsState(Currency(""))
    val toCurrency = viewModel.toCurrency.observeAsState(Currency(""))
    val toAmount = viewModel.operationFlow.observeAsState(BigDecimal.ZERO)

    val sellAmount = rememberSaveable { mutableStateOf("") }
    val transaction = remember { mutableStateOf<Transaction?>(null) }
    val error = remember { mutableStateOf<ExchangeError?>(null) }


    LaunchedEffect(sellAmount.value) {
        sellAmount.value.takeIf { it.isNotEmpty() }?.let {
            it.toBigDecimalOrNull()?.let {
                viewModel.watchOperation(it)
            }
        } ?: viewModel.watchOperation(BigDecimal.ZERO)
    }

    LaunchedEffect(Unit) {
        viewModel.performOperationEvent.collect { event ->
            when (event) {
                is ExchangeResult.Success -> {
                    sellAmount.value = ""
                    transaction.value = event.transaction
                }

                is ExchangeResult.Error -> error.value = event.error
            }
        }
    }

    transaction.value?.let {
        ProcessOperationSuccess(it)
        transaction.value = null
    }
    error.value?.let {
        ProcessOperationError(it)
        error.value = null
    }

    val onFromCurrencySelected: (Currency) -> Unit = { viewModel.pickFromCurrency(it) }
    val onToCurrencySelected: (Currency) -> Unit = { viewModel.pickToCurrency(it) }

    val onPerformOperation: () -> Unit = { viewModel.performOperation() }

    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        WalletsList(wallets.value)
        ExchangeBlock(
            fromCurrency.value,
            toCurrency.value,
            sellAmount,
            toAmount.value,
            wallets.value.keys,
            onFromCurrencySelected,
            onToCurrencySelected,
            modifier = Modifier.padding(top = 16.dp)
        )
        TextButton(
            onClick = onPerformOperation, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.label_exchange).uppercase())
        }
    }
}

@Composable
fun WalletsList(
    wallets: Map<Currency, BigDecimal>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(id = R.string.label_my_balances).uppercase(),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(wallets.toList()) { wallet -> WalletItem(wallet.first, wallet.second) }
        }
    }
}

@Composable
fun ExchangeBlock(
    fromCurrency: Currency,
    toCurrency: Currency,
    sellAmount: MutableState<String>,
    receiveAmount: BigDecimal,
    currencies: Set<Currency>,
    onFromCurrencySelected: (Currency) -> Unit,
    onToCurrencySelected: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(id = R.string.label_exchange).uppercase(),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Text(
                text = stringResource(id = R.string.label_sell).uppercase(),
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = sellAmount.value,
                onValueChange = {
                    sellAmount.value = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.hint_sell),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            CurrencyPicker(fromCurrency, currencies, onFromCurrencySelected, modifier = Modifier.align(Alignment.CenterVertically))
        }
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = stringResource(id = R.string.label_receive).uppercase(),
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = receiveAmount.toPlainString(),
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            )
            CurrencyPicker(toCurrency, currencies, onToCurrencySelected, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

@Composable
fun CurrencyPicker(currency: Currency, currencies: Set<Currency>, onCurrencySelected: (Currency) -> Unit, modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text(text = "Close")
                }
            },
            text = {
                Column {
                    currencies.forEach { currency ->
                        TextButton(onClick = {
                            onCurrencySelected(currency)
                            showDialog.value = false
                        }) {
                            Text(text = currency.code)
                        }
                    }
                }
            }
        )
    }

    Text(
        text = stringResource(id = R.string.picker_value, currency.code),
        textAlign = TextAlign.End,
        modifier = modifier
            .padding(vertical = 8.dp)
            .width(60.dp)
            .clickable { showDialog.value = true }
    )
}

@Composable
fun ProcessOperationSuccess(transaction: Transaction) {
    with(transaction) {
        if (fee <= BigDecimal.ZERO)
            ShowToast(
                stringResource(
                    id = R.string.success_message_f,
                    fromAmount,
                    fromCurrency.code,
                    toAmount,
                    toCurrency.code
                )
            )
        else
            ShowToast(
                stringResource(
                    id = R.string.success_message_fee_f,
                    fromAmount,
                    fromCurrency.code,
                    toAmount,
                    toCurrency.code,
                    fee,
                    fromCurrency.code
                )
            )
    }
}

@Composable
fun ProcessOperationError(error: ExchangeError) {
    // show error message
}

@Composable
fun WalletItem(currency: Currency, amount: BigDecimal) {
    Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.balance_value_f, currency.code, amount))
}

@Composable
fun ShowToast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
@Preview(showBackground = true)
fun WalletItemPreview() {
    WalletItem(Currency("EUR"), BigDecimal(100))
}

@Composable
@Preview(showBackground = true)
fun WalletsListPreview() {
    WalletsList(
        mapOf(
            Currency("EUR") to BigDecimal(100),
            Currency("USD") to BigDecimal(200),
            Currency("GBP") to BigDecimal(300),
            Currency("UAH") to BigDecimal(300)
        ),
    )
}

@Composable
@Preview(showBackground = true)
fun ExchangeBlockPreview() {
    val sellAmount = remember { mutableStateOf("") }
    ExchangeBlock(Currency("EUR"), Currency("USD"), sellAmount, BigDecimal(100), setOf(), {}, {},
        modifier = Modifier.width(400.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun CurrencyPickerPreview() {
    CurrencyPicker(
        Currency("EUR"),
        setOf(Currency("EUR"), Currency("USD"), Currency("GBP")),
        onCurrencySelected = {},
        modifier = Modifier.width(60.dp)
    )
}