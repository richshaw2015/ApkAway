package org.dinorss.apkaway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartAtBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
            // TODO 首次启动后，回到后台
        }
    }
}
