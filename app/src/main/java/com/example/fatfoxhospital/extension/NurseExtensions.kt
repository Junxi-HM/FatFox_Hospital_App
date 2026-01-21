package com.example.fatfoxhospital.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.model.Nurse
import com.example.fatfoxhospital.viewmodel.NurseViewModel

@Composable
fun Nurse.getProfilePainter(): Painter {
    // 1. 获取 ByteArray 中的第一个字节作为索引
    val index = this.profile?.getOrNull(0)?.toInt() ?: 0

    // 2. 从 ViewModel 定义的资源列表中获取对应的 R.drawable ID
    val resId = NurseViewModel.PROFILE_RESOURCES.getOrElse(index) {
        R.drawable.perfil1 // 默认头像
    }

    return painterResource(id = resId)
}