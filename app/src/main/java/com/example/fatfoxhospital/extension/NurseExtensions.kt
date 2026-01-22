package com.example.fatfoxhospital.extension

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.model.Nurse

@Composable
fun Nurse.getProfilePainter(): Painter {
    val profileData = this.profile

    if (profileData != null && profileData.size > 1) {
        try {
            val bitmap = BitmapFactory.decodeByteArray(profileData, 0, profileData.size)
            if (bitmap != null) {
                return BitmapPainter(bitmap.asImageBitmap())
            }
        } catch (e: Exception) {
        }
    }

    return painterResource(id = R.drawable.perfil1)
}