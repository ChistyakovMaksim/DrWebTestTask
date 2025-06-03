package com.chistyakov.drwebtesttask

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chistyakov.drwebtesttask.databinding.ActivityAppBinding
import java.io.File
import java.security.MessageDigest

class AppActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding
    private var packageName: String? = ""
    companion object {
        private const val EXTRA_PACKAGE_NAME = "extra_package_name"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            packageName: String,
        ): Intent {
            return Intent(packageContext, AppActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent != null) {
            packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(packageName!=null)
        {
            val app = getAppInfo(packageName!!, this)
            if(app!=null)
            {
                binding.nameTv.text = app.name
                binding.versionTv.text = app.version
                binding.packageNameTv.text = packageName
                binding.cotrolSum.text = "Контрольная сумма = "+ getApkChecksum(packageName!!, this)
                binding.imageView2.setImageDrawable(app.icon)
                binding.button.setOnClickListener {
                    openApp(packageName!!, this)
                }
            }else
                notFound()
        }else
            notFound()

    }

    fun notFound()
    {
        binding.nameTv.text = "Не найдено"
        binding.versionTv.text = "Не найдено"
        binding.packageNameTv.text = "Не найдено"
        binding.cotrolSum.text = "Не найдено"
    }

    fun openApp(packageName: String, context: Context) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "Приложение не найдено", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAppInfo(packageName: String, context: Context): App? {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val name = pm.getApplicationLabel(appInfo).toString()
            val icon = pm.getApplicationIcon(packageName)
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            val versionName = packageInfo.versionName ?: "N/A"
            App(name, packageName, versionName, icon)
        } catch (e: Exception) {
            e.printStackTrace()
            null // если приложение не найдено
        }
    }

    fun getApkChecksum(packageName: String, context: Context): String? {
        return try {
            val pm = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }

            val apkPath = packageInfo.applicationInfo!!.sourceDir
            val apkFile = File(apkPath)

            val digest = MessageDigest.getInstance("SHA-256")
            apkFile.inputStream().use { inputStream ->
                val buffer = ByteArray(8192)
                var read: Int
                while (inputStream.read(buffer).also { read = it } > 0) {
                    digest.update(buffer, 0, read)
                }
            }

            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}