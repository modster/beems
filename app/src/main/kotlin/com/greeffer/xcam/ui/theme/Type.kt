package com.greeffer.xcam.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.greeffer.xcam.R

private val provider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs
)

private val fontName = GoogleFont("Space Grotesk")

private val AppFontFamily = FontFamily(
  Font(googleFont = fontName, fontProvider = provider)
)

val Typography = Typography(
  headlineLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (- 0.64).sp
  ),
  headlineMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 31.sp
  ),
  titleMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.9.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
  ),
  labelLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 12.sp,
    letterSpacing = 1.2.sp
  ),
  labelSmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 10.sp,
    letterSpacing = 0.sp
  )
)

val readoutLarge = TextStyle(
  fontFamily = AppFontFamily,
  fontWeight = FontWeight.Medium,
  fontSize = 18.sp,
  lineHeight = 18.sp,
  letterSpacing = 0.05.em
)

val labelCaps = TextStyle(
  fontFamily = AppFontFamily,
  fontWeight = FontWeight.Bold,
  fontSize = 12.sp,
  lineHeight = 12.sp,
  letterSpacing = 0.1.em
)
