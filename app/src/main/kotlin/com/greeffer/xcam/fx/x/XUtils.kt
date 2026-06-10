package com.greeffer.xcam.fx.x

import android.content.Context
import com.greeffer.xcam.R
import java.io.File


fun getOutputDirectory(context: Context): File
{
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let { mFile ->
        File(mFile, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
    {
        mediaDir
    }
    else
    {
        context.filesDir
    }
}
