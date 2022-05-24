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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        if (hasAllPermissions()) {
            // 启动服务，有些手机服务需要启动两次才生效
            startService(Intent(this, ApkBlockerService::class.java))
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
