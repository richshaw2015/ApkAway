package org.dinorss.apkaway

import android.os.CountDownTimer
import android.os.FileObserver
import android.os.Looper
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

// 初始扫描路径
const val SDCARD_DATA_ROOT = "/sdcard/Android/data"
val OBSERVER_EXT_LIST = setOf("/sdcard/Download", "/sdcard/Bing")

// 重置 apk 文件相关
const val RESET_MAX = 20
const val RESET_TIMER_MS = 1000.0
const val RESET_TIMER_INTERVAL = 50.0

// 日志最大数量
const val LOG_MAX = 100

// 拦截记录
var BLOCK_LIST  = arrayListOf<String>()

class RecursiveApkObserver:
    FileObserver(SDCARD_DATA_ROOT, CREATE) {
    private val mObservers: MutableMap<String, FileObserver?> = HashMap()

    private fun startWatching(path: String) {
        synchronized(mObservers) {
            if (watch(File(path), true)) {
                var observer = mObservers.remove(path)
                observer?.stopWatching()
                observer = SingleApkObserver(path)
                observer.startWatching()
                mObservers.put(path, observer)
            }
        }
    }

    override fun startWatching() {
        val stack = Stack<String>()
        stack.push(SDCARD_DATA_ROOT)
        for (dir in OBSERVER_EXT_LIST) {
            stack.push(dir)
        }

        while (!stack.empty()) {
            val parent = stack.pop()
            startWatching(parent)
            val path = File(parent)
            val files = path.listFiles()
            if (files != null) {
                for (file in files) {
                    if (watch(file, false)) {
                        stack.push(file.absolutePath)
                    }
                }
            }
        }
    }
    private fun watch(file: File, really: Boolean = false): Boolean {
        if (really) {
            // 只监听应用目录 files 下的一级目录
            // 只监控特定的文件夹，避免影响性能
            if (file.name.lowercase().contains("download")
                || OBSERVER_EXT_LIST.contains(file.absolutePath)) {
                Log.d("", file.absolutePath)
                return true
            }
            return false
        } else {
            // 计算路径
            if (file.isDirectory && file.name != "." && file.name != "..") {
                if (file.parent!!.endsWith("/files")
                    || SDCARD_DATA_ROOT.contains(file.parent!!.toString())
                    || file.toString().endsWith("/files")) {
                    return true
                }
            }
            return false
        }
    }

    override fun stopWatching() {
        synchronized(mObservers) {
            for (observer in mObservers.values) {
                observer!!.stopWatching()
            }
            mObservers.clear()
        }
    }

    override fun onEvent(event: Int, path: String?) {
        val file: File = if (path == null) {
            File(SDCARD_DATA_ROOT)
        } else {
            File(SDCARD_DATA_ROOT, path)
        }
        notify(event, file)
    }

    private fun notify(event: Int, file: File) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val log = "⛔ ${LocalDateTime.now().format(formatter)} 已拦截安装包 $file"
        BLOCK_LIST.add(0, log)
        if (BLOCK_LIST.size > LOG_MAX) {
            BLOCK_LIST = ArrayList(BLOCK_LIST.dropLast(1))
        }

        Log.i("$event", log)

        // 只清理 apk 文件
        if (file.name.contains(".apk")) {
            // 先立刻清理
            for (i in 1..RESET_MAX) {
                file.writeText("")
            }

            // 继续用定时器定时清理
            thread {
                Looper.prepare()

                val timer = object: CountDownTimer(RESET_TIMER_MS.toLong(),
                    RESET_TIMER_INTERVAL.toLong()) {
                    override fun onTick(ms: Long) {
                        try {
                            file.writeText("")
                        } catch (e: FileNotFoundException) {
                            Log.d("onTick", e.toString())
                        }

                    }
                    override fun onFinish() {}
                }
                timer.start()

                Looper.loop()
            }
        }
    }

    private inner class SingleApkObserver(private val filePath: String) :
        FileObserver(filePath, CREATE) {
        // TODO 重构这个废弃的 api，29 以上才支持 File 类型的参数
        override fun onEvent(event: Int, path: String?) {
            val file: File = if (path == null) {
                File(filePath)
            } else {
                File(filePath, path)
            }
            when (event and ALL_EVENTS) {
                CREATE -> {
                    notify(event, file)
                }
            }
        }
    }
}