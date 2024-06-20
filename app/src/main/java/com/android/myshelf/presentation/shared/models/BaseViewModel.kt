package com.android.myshelf.presentation.shared.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface BaseIntent

interface BaseState

interface BaseLabel

abstract class BaseViewModel<Intent : BaseIntent, State : BaseState, Label : BaseLabel> :
    ViewModel() {

    private val initialState by lazy { setInitialState() }

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _labels = Channel<Label>()
    val labels = _labels.receiveAsFlow()

    abstract fun setInitialState(): State

    abstract fun onIntent(intent: Intent)

    protected fun updateState(state: State) {
        _state.update { state }
    }

    protected fun updateState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun publishLabel(label: Label) {
        viewModelScope.launch { _labels.send(label) }
    }
}