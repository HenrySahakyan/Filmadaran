package com.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

sealed class ViewModelEvent {
    data class ShowToast(val message: String) : ViewModelEvent()
}

abstract class BaseViewModel : ViewModel(), KoinComponent {

    protected val _viewState = MutableStateFlow<ViewState>(ViewState.Complete)
    val viewState = _viewState.asStateFlow()

    private val _events = MutableSharedFlow<ViewModelEvent>()
    val events = _events.asSharedFlow()

    protected fun sendEvent(event: ViewModelEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            block()
        }
    }

    protected fun <T> call(
        request: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        launch {
            _viewState.value = ViewState.Loading
            try {
                val response = request()
                onSuccess(response)
                _viewState.value = ViewState.Complete
            } catch (e: Exception) {
                val message = e.message ?: "Unknown error"
                onError(message)
                _viewState.value = ViewState.Error(message)
            }
        }
    }

    protected fun <T> launchInIO(block: suspend CoroutineScope.() -> T): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
}
