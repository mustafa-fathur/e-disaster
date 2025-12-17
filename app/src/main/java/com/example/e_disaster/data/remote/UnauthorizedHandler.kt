package com.example.e_disaster.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton object to handle global unauthorized (401) events.
 * This acts as an event bus between the network layer and the UI layer.
 */
@Singleton
class UnauthorizedHandler @Inject constructor() {
    private val _onUnauthorized = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onUnauthorized = _onUnauthorized.asSharedFlow()

    fun trigger() {
        _onUnauthorized.tryEmit(Unit)
    }
}