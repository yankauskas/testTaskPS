package org.yankauskas.pstest.presentation.exchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.ExchangeResult
import org.yankauskas.pstest.domain.model.Operation
import org.yankauskas.pstest.domain.model.Resource
import org.yankauskas.pstest.domain.usecase.PerformOperationUseCase
import org.yankauskas.pstest.domain.usecase.StartPollingUseCase
import org.yankauskas.pstest.domain.usecase.WatchOperationUseCase
import org.yankauskas.pstest.domain.usecase.WatchWalletsUseCase
import java.math.BigDecimal

class ExchangeViewModel(
    private val performOperationUseCase: PerformOperationUseCase,
    private val startPollingUseCase: StartPollingUseCase,
    private val watchOperationUseCase: WatchOperationUseCase,
    private val watchWalletsUseCase: WatchWalletsUseCase
) : ViewModel() {
    private val _walletsFlow = watchWalletsUseCase()
    val walletsFlow = _walletsFlow

    private val _operationFlow: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)
    val operationFlow: LiveData<BigDecimal> = _operationFlow

    private val _pollingLiveData: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val pollingLiveData: LiveData<Resource<Unit>> = _pollingLiveData

    private val _fromCurrency: MutableLiveData<Currency> = MutableLiveData()
    val fromCurrency: LiveData<Currency> = _fromCurrency

    private val _toCurrency: MutableLiveData<Currency> = MutableLiveData()
    val toCurrency: LiveData<Currency> = _toCurrency

    private val _performOperationEvent: MutableSharedFlow<ExchangeResult> = MutableSharedFlow()
    val performOperationEvent: SharedFlow<ExchangeResult> = _performOperationEvent.asSharedFlow()

    private var currentOperation: Operation = Operation.EMPTY

    private var operationJob: Job? = null

    init {
        viewModelScope.launch {
            startPollingUseCase().collectLatest { _pollingLiveData.postValue(it) } }

        pickFromCurrency(_walletsFlow.value.entries.maxBy { it.value }.key)
        pickToCurrency(_walletsFlow.value.keys.first())
    }

    fun watchOperation(amount: BigDecimal) {
        currentOperation = currentOperation.copy(amount = amount)
        watchOperation(currentOperation)
    }

    fun pickFromCurrency(currency: Currency) {
        _fromCurrency.postValue(currency)
        currentOperation = currentOperation.copy(from = currency)
        watchOperation(currentOperation)
    }

    fun pickToCurrency(currency: Currency) {
        _toCurrency.postValue(currency)
        currentOperation = currentOperation.copy(to = currency)
        watchOperation(currentOperation)
    }

    fun performOperation() {
        viewModelScope.launch {
            _performOperationEvent.emit(performOperationUseCase(currentOperation))
        }
    }

    private fun watchOperation(operation: Operation) {
        operationJob?.cancel()

        operationJob = viewModelScope.launch {
            watchOperationUseCase(operation).collectLatest { _operationFlow.postValue(it) }
        }
    }
}