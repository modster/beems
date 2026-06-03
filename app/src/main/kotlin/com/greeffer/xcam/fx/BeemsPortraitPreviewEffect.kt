package com.greeffer.xcam.fx

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PortraitPreviewEffect {
    companion object {
        fun getExecutor(): Executor = Executors.newSingleThreadExecutor()
    }
}
