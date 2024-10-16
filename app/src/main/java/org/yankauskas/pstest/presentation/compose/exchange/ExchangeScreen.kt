package org.yankauskas.pstest.presentation.compose.exchange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.yankauskas.pstest.R
import org.yankauskas.pstest.domain.model.Currency
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
    val amount = viewModel.operationFlow.observeAsState(BigDecimal.ZERO)

    val onSellAmountChange: (String) -> Unit = {
        it.takeIf { it.isNotEmpty() }?.let {
            it.toBigDecimalOrNull()?.let {
                viewModel.watchOperation(it)
            }
        } ?: viewModel.watchOperation(BigDecimal.ZERO)
    }

    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        WalletsList(wallets.value)
        ExchangeBlock(fromCurrency.value, toCurrency.value, amount.value, onSellAmountChange, modifier = Modifier.padding(top = 16.dp))
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
    receiveAmount: BigDecimal,
    onSellAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val sellAmount = rememberSaveable { mutableStateOf("") }

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
                    onSellAmountChange(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .weight(1f),
                placeholder = { Text(text = stringResource(id = R.string.hint_sell)) }
            )
            CurrencyPicker(fromCurrency, modifier = Modifier.align(Alignment.CenterVertically))
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
            CurrencyPicker(toCurrency, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

@Composable
fun CurrencyPicker(currency: Currency, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.picker_value, currency.code),
        textAlign = TextAlign.End,
        modifier = modifier
            .padding(vertical = 8.dp)
            .width(60.dp)
    )
}

@Composable
fun WalletItem(currency: Currency, amount: BigDecimal) {
    Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.balance_value_f, currency.code, amount))
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
    ExchangeBlock(Currency("EUR"), Currency("USD"), BigDecimal(100), {}, modifier = Modifier.width(300.dp))
}

@Composable
@Preview(showBackground = true)
fun CurrencyPickerPreview() {
    CurrencyPicker(Currency("EUR"))
}