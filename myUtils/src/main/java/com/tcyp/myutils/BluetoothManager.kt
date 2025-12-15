package com.tcyp.myutils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tcyp.myutils.PermissionManager.hasPermission
import kotlin.math.min

/**
 * <蓝牙管理工具类>
 * 提供蓝牙开关、扫描、配对等功能
 * 注意android 12 的蓝牙的操作变更
 * @author focustech
 * @version [版本号, 2024-11-20]
 * 需要现在application进行注册
 * @since [V1]
</蓝牙管理工具类> */
class BluetoothManager(app:  Application) {
    private val TAG = "BluetoothHelper"
    // 隐藏的关闭蓝牙 Intent（Android 12+ 专用）
    private val ACTION_REQUEST_DISABLE = "android.bluetooth.adapter.action.REQUEST_DISABLE"
    private var context: Context = app.applicationContext
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanCallback: BluetoothScanCallback? = null
    private var scanReceiver: BroadcastReceiver? = null

    private val stateListeners = mutableSetOf<(Int) -> Unit>()

    // ActivityResult 启动器（现代 API，需在 Activity/Fragment 中注册）
    private var enableLauncher: ActivityResultLauncher<Intent>? = null
    private var disableLauncher: ActivityResultLauncher<Intent>? = null

    // 广播接收器（监听蓝牙状态变化）
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                Log.d(TAG, "蓝牙状态变化: ${stateToString(state)}")
                stateListeners.forEach { it(state) }
            }
        }
    }

    init {
        val bluetoothManager = this.context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        this.bluetoothAdapter = bluetoothManager?.adapter

        //注册广播监听
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, filter)
        Log.i(TAG, "注册蓝牙状态广播")
    }

    fun setupLauncher(activity: androidx.activity.ComponentActivity,
                      onEnableResult: ((Boolean) -> Unit)? = null,
                      onDisableResult: ((Boolean) -> Unit)? = null
    ) {
        enableLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val granted = result.resultCode == Activity.RESULT_OK
            onEnableResult?.invoke(granted)
        }

        disableLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val granted = result.resultCode == Activity.RESULT_OK
            onDisableResult?.invoke(granted)
        }
    }

    /**
     * 蓝牙扫描回调接口
     */
    interface BluetoothScanCallback {
        /**
         * 发现新设备
         *
         * @param device 蓝牙设备
         */
        fun onDeviceFound(device: BluetoothDevice?)

        /**
         * 扫描完成
         */
        fun onScanFinished()

        /**
         * 扫描失败
         *
         * @param errorMsg 错误信息
         */
        fun onScanFailed(errorMsg: String?)
    }

    val isBluetoothSupported: Boolean
        /**
         * 检查设备是否支持蓝牙
         *
         * @return true表示支持，false表示不支持
         */
        get() = bluetoothAdapter != null

    val isBluetoothEnabled: Boolean
        /**
         * 检查蓝牙是否已开启
         *
         * @return true表示已开启，false表示未开启
         */
        get() = bluetoothAdapter != null && bluetoothAdapter?.isEnabled ?: false

    /**
     * 开启蓝牙，只在android 11及以下生效
     *
     * @return true表示成功，false表示失败
     */
    fun enableBluetooth(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        if (bluetoothAdapter!!.isEnabled) {
            return true
        }
        if (!bluetoothAdapter!!.isEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return bluetoothAdapter!!.enable()
        }
        return true
    }

    /**
     * 请求开启蓝牙（需要用户确认）
     *
     * @return Intent对象，需要在Activity中使用startActivityForResult启动
     */
    fun requestEnableBluetooth(activity: androidx.activity.ComponentActivity) {
        if (bluetoothAdapter == null || bluetoothAdapter!!.isEnabled) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent =  Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableLauncher?.launch(intent)?: Log.e(TAG, "请先调用 setupLaunchers() 初始化 launcher")

        } else {
            enableBluetooth()
        }

    }

    /**
     * 请求关闭蓝牙（需要用户确认）
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestDisableBluetooth(activity: androidx.activity.ComponentActivity) {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+：使用隐藏 Intent
            val intent = Intent(ACTION_REQUEST_DISABLE)
            disableLauncher?.launch(intent)
                ?: Log.e(TAG, "请先调用 setupLaunchers() 初始化 launcher")
        } else {
            // Android 11 及以下：尝试直接关闭（需要 BLUETOOTH_ADMIN 权限）
            if (hasPermission(context,Manifest.permission.BLUETOOTH_ADMIN)) {
                bluetoothAdapter?.disable()
            } else {
                Log.w(TAG, "无 BLUETOOTH_ADMIN 权限，无法直接关闭蓝牙")
            }
        }
    }

    /** 添加蓝牙状态监听 */
    fun addStateListener(listener: (state: Int) -> Unit) {
        stateListeners.add(listener)
    }

    fun removeStateListener(listener: (state: Int) -> Unit) {
        stateListeners.remove(listener)
    }


    /**
     * 检查是否有蓝牙权限
     *
     * @return true表示有权限，false表示无权限
     */
    fun hasBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12及以上需要BLUETOOTH_SCAN和BLUETOOTH_CONNECT权限
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                            == PackageManager.PERMISSION_GRANTED)
        } else {
            // Android 12以下需要BLUETOOTH和ACCESS_FINE_LOCATION权限
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                            == PackageManager.PERMISSION_GRANTED)
        }
    }

    val pairedDevices: MutableSet<BluetoothDevice?>?
        /**
         * 获取已配对的设备列表
         *
         * @return 已配对设备集合，如果蓝牙不可用则返回null
         */
        get() {
            if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
                return null
            }
            try {
                return bluetoothAdapter!!.bondedDevices
            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
        }

    /**
     * 开始扫描蓝牙设备
     *
     * @param callback 扫描回调
     * @return true表示成功开始扫描，false表示失败
     */
    fun startScan(callback: BluetoothScanCallback?): Boolean {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            callback?.onScanFailed("蓝牙未开启或不可用")
            return false
        }

        if (!hasBluetoothPermission()) {
            callback?.onScanFailed("缺少蓝牙权限")
            return false
        }

        this.scanCallback = callback

        // 注册广播接收器
        scanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.getAction()
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice?>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null && scanCallback != null) {
                        scanCallback!!.onDeviceFound(device)
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                    if (scanCallback != null) {
                        scanCallback!!.onScanFinished()
                    }
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(scanReceiver, filter)

        try {
            return bluetoothAdapter!!.startDiscovery()
        } catch (e: SecurityException) {
            e.printStackTrace()
            callback?.onScanFailed("权限不足: " + e.message)
            return false
        }
    }

    /**
     * 停止扫描蓝牙设备
     */
    fun stopScan() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (bluetoothAdapter != null && bluetoothAdapter!!.isDiscovering()) {
            try {
                bluetoothAdapter!!.cancelDiscovery()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        if (scanReceiver != null) {
            try {
                context.unregisterReceiver(scanReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            scanReceiver = null
        }
        scanCallback = null
    }

    val isScanning: Boolean
        /**
         * 判断是否正在扫描
         *
         * @return true表示正在扫描，false表示未扫描
         */
        get() {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return bluetoothAdapter != null && bluetoothAdapter!!.isDiscovering
        }

    /**
     * 使设备可被其他蓝牙设备发现
     *
     * @param duration 可发现时长（秒），最大300秒
     * @return Intent对象，需要在Activity中使用startActivityForResult启动
     */
    fun getDiscoverableIntent(duration: Int): Intent {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, min(duration, 300))
        return intent
    }

    val localDeviceName: String?
        /**
         * 获取本地蓝牙设备名称
         *
         * @return 设备名称，如果蓝牙不可用则返回null
         */
        get() {
            if (bluetoothAdapter == null) {
                return null
            }
            try {
                return bluetoothAdapter!!.name
            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
        }

    /**
     * 设置本地蓝牙设备名称
     *
     * @param name 设备名称
     * @return true表示成功，false表示失败
     */
    fun setLocalDeviceName(name: String?): Boolean {
        if (bluetoothAdapter == null || GeneralUtils.isNullOrEmpty(name)) {
            return false
        }
        try {
            return bluetoothAdapter!!.setName(name)
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        }
    }

    val localDeviceAddress: String?
        /**
         * 获取本地蓝牙MAC地址
         *
         * @return MAC地址，如果蓝牙不可用则返回null
         */
        @SuppressLint("HardwareIds")
        get() {
            if (bluetoothAdapter == null) {
                return null
            }
            try {
                return bluetoothAdapter!!.address
            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
        }

    private fun stateToString(state: Int): String = when (state) {
        BluetoothAdapter.STATE_OFF -> "已关闭"
        BluetoothAdapter.STATE_ON -> "已开启"
        BluetoothAdapter.STATE_TURNING_ON -> "正在开启"
        BluetoothAdapter.STATE_TURNING_OFF -> "正在关闭"
        else -> "未知状态"
    }

    /**
     * 释放资源
     */
    fun release() {
        stopScan()
    }
}
