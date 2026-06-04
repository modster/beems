package com.greeffer.xcam.fx.x

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class XEffect {
    companion object {
        fun getExecutor(): Executor = Executors.newSingleThreadExecutor()
    }
}
