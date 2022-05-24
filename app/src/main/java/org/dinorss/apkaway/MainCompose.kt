package org.dinorss.apkaway

import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(hasAllPermissions: Boolean = false, logs: List<String>) {
    val bottomState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    BottomSheetScaffold(
        scaffoldState = bottomState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            HelpPage(bottomState = bottomState)
        },
    ) {
        Scaffold(
            content = {
                Column {
                    TopBanner(hasAllPermissions = hasAllPermissions, bottomState = bottomState)
                    MiddleDeclaration()
                    if (!hasAllPermissions) BottomLottie() else BottomLogs(logs = logs)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopBanner(hasAllPermissions: Boolean = false, bottomState: BottomSheetScaffoldState) {
    val coroutineScope = rememberCoroutineScope()

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
                .clickable {
                    coroutineScope.launch { bottomState.bottomSheetState.expand() }
                }
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
                "❤️ 小卓启动后会自动拦截恶意安装包，守护长者和儿童不受恶意应用骚扰",
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
        LottieAnimation(composition, iterations = LottieConstants.IterateForever)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HelpPage(bottomState: BottomSheetScaffoldState) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
            .verticalScroll(rememberScrollState())) {

        Row(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("关于小卓守护", fontSize = 22.sp)
            Text("❌", fontSize = 22.sp, modifier = Modifier.clickable {
                coroutineScope.launch { bottomState.bottomSheetState.collapse() }
            })
        }

        Divider(
            color = MaterialTheme.colors.onBackground, modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .padding(bottom = 12.dp)
        )

        Text(
            "❓️ 小卓是怎么诞生的",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colors.onBackground
        )
        Text(
            "家中长者和儿童的手机总是会莫名出现很多恶意应用（通常是垃圾清理类），这些应用每隔十几秒就会弹出一个全屏广告，严重影响了手机的正常使用。\n我意识到指责他们是没有用的，作为一名开发者，必须要做点什么，于是小卓守护便诞生了。",
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colors.onBackground
        )

        Text(
            "❓️ 小卓是如何运作的",
            fontSize = 18.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colors.onBackground
        )
        Text(
            "作为一个非系统级应用，能做的并不多，目前的方案是监控应用的存储目录，一旦发现有恶意 APK 文件生成就立刻拦截，使其安装失败。\n不过这个方案在 Android 11 以上效果不佳。",
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colors.onBackground
        )

        Text(
            "❓️ 还有其他办法吗",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colors.onBackground
        )
        Text(
            "关闭“安装未知应用”权限有一些帮助；\nROOT 手机是最彻底的；\n更换 iPhone 是最简单的；",
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colors.onBackground
        )

        Text(
            "❓️ 支持和反馈",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colors.onBackground
        )
        Text(
            "如果小卓帮助到了您的家人，或者拦截没有生效，或者有更好的想法，都欢迎在 GitHub 上交流。",
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colors.onBackground
        )
    }
}
