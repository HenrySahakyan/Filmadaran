package com.core.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {
    protected suspend fun <T> io(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            block()
        }
    }
}
