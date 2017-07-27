package com.example.pintodesktop

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.example.pintodesktop.support.Absent
import com.example.pintodesktop.support.Optional
import com.example.pintodesktop.support.Present
import java.util.*

class ShareActivity : AppCompatActivity() {

    private lateinit var adapter: AppListAdapter

    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val appList = findViewById<RecyclerView>(R.id.app_list)
        adapter = AppListAdapter(LayoutInflater.from(this))
        appList.adapter = adapter
        appList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        appList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        saveButton = findViewById<Button>(R.id.save)
    }

    override fun onStart() {
        super.onStart()

        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
                .findUrl()
                .map { Uri.parse(it) }

        when (url) {
            is Absent -> {
                Toast.makeText(this, "Unsupported :(((", Toast.LENGTH_SHORT).show()
                finish()
            }
            is Present -> {
                val packageManager = packageManager
                val intent = Intent(Intent.ACTION_VIEW, url.value)
                val queryIntentActivities = packageManager.queryIntentActivities(intent, 0)
                val viewModels = queryIntentActivities
                        .map {
                            AppListAdapter.AppViewModel(
                                    it.loadIcon(packageManager),
                                    it.loadLabel(packageManager)
                            )
                        }


                adapter.updateWith(viewModels)

                saveButton.setOnClickListener {
                    if (!ShortcutManagerCompat.isRequestPinShortcutSupported(this) || queryIntentActivities.isEmpty()) {
                        return@setOnClickListener
                    }

                    val shortcut = ShortcutInfoCompat.Builder(this, UUID.randomUUID().toString())
                            .setIntent(intent)
                            .setIcon(IconCompat.createWithBitmap(queryIntentActivities.first().loadIcon(packageManager).toBitmap()))
                            .setShortLabel("Coso")
                            .build()

                    ShortcutManagerCompat.requestPinShortcut(this, shortcut, null)
                    finish()
                }
            }
        }
    }

    private fun String.findUrl(): Optional<String> {
        val matcher = Patterns.WEB_URL.matcher(this)
        if (matcher.find()) {
            val url = substring(matcher.start(), matcher.end())
            return Present(url)
        } else {
            return Absent.absent()
        }
    }

    private fun Drawable.toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        draw(canvas)
        return bitmap
    }
}
