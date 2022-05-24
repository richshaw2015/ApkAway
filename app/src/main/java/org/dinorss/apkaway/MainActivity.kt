package org.dinorss.apkaway

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Nullable
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.dinorss.apkaway.ui.theme.ApkAwayTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查权限
        checkAllPermissions()
    }

    override fun onResume() {
        super.onResume()

        // 刷新界面
        setContent {
            ApkAwayTheme {
                HomePage(hasAllPermissions = hasAllPermissions(), logs= BLOCK_LIST)
            }
        }
        if (hasAllPermissions()) {
            // 启动服务
            startService(Intent(this, ApkBlockerService::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (hasAllPermissions()) {
                // 启动服务，有些手机服务需要启动两次才生效
                startService(Intent(this, ApkBlockerService::class.java))
            }
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        try {
            val manager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }
    private fun hasAllPermissions(): Boolean {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                return false
            }
        }
        if (SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                return false
            }
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_DENIED) {
            return false
        }
        return true
    }

    private fun checkAllPermissions() {
        checkPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED, 0)

        if (SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1)
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 2)
        }

        if (SDK_INT >= Build.VERSION_CODES.P) {
            checkPermission(Manifest.permission.FOREGROUND_SERVICE, 3)
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivityForResult(intent, 4)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("PERM", "$requestCode")

        // 权限更新
        setContent {
            ApkAwayTheme {
                HomePage(hasAllPermissions = hasAllPermissions(), logs= BLOCK_LIST)
            }
        }
        if (hasAllPermissions()) {
            // 启动服务
            startService(Intent(this, ApkBlockerService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("PERM", "$requestCode $resultCode $data")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            setContent {
                ApkAwayTheme {
                    HomePage(hasAllPermissions = hasAllPermissions(), logs= BLOCK_LIST)
                }
            }
            if (hasAllPermissions()) {
                // 启动服务
                startService(Intent(this, ApkBlockerService::class.java))
            }
        }
    }

    companion object{
        const val  ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.stopservice"
    }
}

@Composable
fun HomePage(hasAllPermissions: Boolean = false, logs: List<String>) {
    Scaffold(
        content = {
            Column {
                TopBanner(hasAllPermissions = hasAllPermissions)
                MiddleDeclaration(hasAllPermissions = hasAllPermissions)
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
fun MiddleDeclaration(hasAllPermissions: Boolean = false) {
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
            if (SDK_INT >= Build.VERSION_CODES.R)
                Text(
                    "ℹ️️ 由于技术原因，Android 11 及以上版本拦截效果不佳，请了解",
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
