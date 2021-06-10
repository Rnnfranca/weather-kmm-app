package me.user.shared

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO