package com.chistyakov.drwebtesttask

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chistyakov.drwebtesttask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var rvAdapter: AppsRecyclerAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        rvAdapter = AppsRecyclerAdapter(getInstalledAppsInfo(this), { app ->
            onAppClick(app)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = rvAdapter
    }

    fun getInstalledAppsInfo(context: Context): MutableList<App> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appInfoList = mutableListOf<App>()

        for (app in apps) {
            try {
                val name = pm.getApplicationLabel(app).toString()
                val packageName = app.packageName
                val icon = try {
                    pm.getApplicationIcon(app)
                } catch (e: PackageManager.NameNotFoundException) {
                    ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)!!
                }
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(app.packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(app.packageName, 0)
                }
                val versionName = packageInfo.versionName ?: "N/A"
                appInfoList.add(App(name, packageName, versionName, icon))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        appInfoList.sortBy { it.name.lowercase() }
        return appInfoList
    }

    private fun onAppClick(app: App) {
        startActivity(
            AppActivity.forIntent(
                packageContext = this,
                packageName = app.packageName
            )
        )
    }
}