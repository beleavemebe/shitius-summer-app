package company.vk.education.siriusapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : BaseViewState, Intent : BaseViewIntent, Err> : ViewModel() {

    abstract val initialState: State

    private val _viewState: MutableStateFlow<State> by lazy { MutableStateFlow(initialState) }
    val viewState: StateFlow<State> = _viewState

    abstract fun accept(intent: Intent): Any

    @ShitiusDsl
    protected fun reduce(f: (prevState: State) -> State) {
        val newState = f(_viewState.value)
        _viewState.value = newState
    }

    abstract fun mapThrowable(throwable: Throwable): Err

    private val _errors = MutableSharedFlow<Err>(replay = 1)

    val errors: Flow<Err> = _errors

    fun execute(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            val err = mapThrowable(throwable)
            _errors.emit(err)
        }
    }
}