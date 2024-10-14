package org.yankauskas.pstest.presentation.compose.exchange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

    Column(modifier = modifier.padding(16.dp)) {
        WalletsList(wallets.value)
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