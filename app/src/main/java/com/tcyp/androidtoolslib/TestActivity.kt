package com.tcyp.androidtoolslib

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tcyp.myutils.ToastUtils
import com.tcyp.myutils.image.ColiUtils.loadCircleIcon
import com.tcyp.myutils.image.ColiUtils.loadRounded
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    private var netBtn : AppCompatTextView? = null

    private var netResult : AppCompatTextView? = null

    private var img: AppCompatImageView? = null
    private val viewModel: TestViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        netBtn = findViewById<AppCompatTextView>(R.id.get_user)
        netResult = findViewById<AppCompatTextView>(R.id.get_user_result)
        img = findViewById<AppCompatImageView>(R.id.test_img)
        netBtn?.setOnClickListener {
//            viewModel.getUsers()
            ToastUtils.showToast("测试", Toast.LENGTH_LONG)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 获取用户列表
                viewModel.list.collect() {
                    netResult?.text = it.toString()
                }
            }
        }

        img?.loadCircleIcon(
            "https://himg.bdimg.com/sys/portrait/item/pp.1.c3ff66de.I1qFwZ-QpjksucJ-WiAcvA.jpg?tt=1766745686079",
            placeholderRes = R.drawable.ic_launcher_background,
            errorRes = R.drawable.ic_launcher_background,
        )

    }
}