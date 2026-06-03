package com.greeffer.empty_activity

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PortraitPreviewEffect {
    companion object {
        fun getExecutor(): Executor = Executors.newSingleThreadExecutor()
    }
}
