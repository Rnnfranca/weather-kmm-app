package me.user.shared

import kotlinx.coroutines.CoroutineDispatcher

internal actual val coroutineDispatcher: CoroutineDispatcher =
    NsQueueDispatcher(
        dispatch_get_main_queue()
    )