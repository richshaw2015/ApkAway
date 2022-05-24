package org.dinorss.apkaway

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun HomePage(hasAllPermissions: Boolean = false, logs: List<String>) {
    Scaffold(
        content = {
            Column {
                TopBanner(hasAllPermissions = hasAllPermissions)
                MiddleDeclaration()
                if (!hasAllPermissions) BottomLottie() else BottomLogs(logs = logs)
            }
        },
    )
}

@Composable
fun TopBanner(hasAllPermissions: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxWidth()) {
        Image(
            painter = if (hasAllPermissions) painterResource(R.drawable.ic_logo) else painterResource(
                R.drawable.ic_logo_grey
            ),
            contentDescription = "Logo",
            Modifier
                .size(108.dp, 108.dp)
                .padding(16.dp)
        )
        Column {
            Text("小卓･" + (if (hasAllPermissions) "正在守护中" else "服务未启动"), fontSize = 24.sp)
            Text("自动拦截恶意安装包", fontSize = 18.sp, color = MaterialTheme.colors.onPrimary)
        }
    }
}

@Composable
fun MiddleDeclaration() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .background(MaterialTheme.colors.background)
        .fillMaxWidth()) {
        Column(modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)) {
            Text(
                "❤️ 应用启动后会自动拦截恶意安装包，守护长者和孩童不受恶意应用骚扰",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colors.onBackground
            )
            // 根据权限状态动态显示
            Text(
                "⚠️ 请确保开启了应用的存储权限、开机启动权限、前台服务权限",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colors.onBackground
            )

            // 根据版本号动态显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Text(
                    "ℹ️️ 由于技术原因，Android 11 及以上版本拦截效果不佳（当前 Android ${Build.VERSION.RELEASE_OR_CODENAME}）",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colors.onBackground
                ) else Unit

            Divider(color = MaterialTheme.colors.onBackground, modifier = Modifier
                .fillMaxWidth()
                .width(1.dp))
        }
    }
}

@Composable
fun BottomLogs(logs : List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())) {
        if (logs.isNotEmpty()) {
            logs.forEach { log ->
                Text(log,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
        } else {
            Text("⛔ 拦截日志将在这里显示",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(vertical = 6.dp),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun BottomLottie() {
    // TODO 这个状态可能导致问题
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.dog_lottie))

    Column(modifier = Modifier
        .fillMaxHeight()
        .wrapContentSize(Alignment.Center)
        .padding(horizontal = 64.dp)) {
        LottieAnimation(composition, iterations = LottieConstants.IterateForever,)
    }
}
