package org.dinorss.apkaway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.dinorss.apkaway.ui.theme.ApkAwayTheme
import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 界面逻辑
        setContent {
            ApkAwayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }

        // 检查权限
        checkAllPermissions()

        Log.d("", hasAllPermissions().toString())

        // 启动服务
        startService(Intent(this, ApkBlockerService::class.java))
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                return false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        // TODO 权限更新后，刷新页面
        checkPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1)
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 2)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkPermission(Manifest.permission.FOREGROUND_SERVICE, 3)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivity(intent)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }

    companion object{
        const val  ACTION_STOP_FOREGROUND = "${BuildConfig.APPLICATION_ID}.stopforeground"
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ApkAwayTheme {
        Greeting("Android")
    }
}