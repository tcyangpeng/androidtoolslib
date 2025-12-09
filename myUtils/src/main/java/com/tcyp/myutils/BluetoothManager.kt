package com.tcyp.myutils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.min

/**
 * <蓝牙管理工具类>
 * 提供蓝牙开关、扫描、配对等功能
 *
 * @author focustech
 * @version [版本号, 2024-11-20]
 * @since [V1]
</蓝牙管理工具类> */
class BluetoothManager(context: Context) {
    private val context: Context
    private val bluetoothAdapter: BluetoothAdapter?
    private var scanCallback: BluetoothScanCallback? = null
    private var scanReceiver: BroadcastReceiver? = null

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

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    init {
        this.context = context.getApplicationContext()
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
        get() = bluetoothAdapter != null && bluetoothAdapter.isEnabled()

    /**
     * 开启蓝牙
     *
     * @return true表示成功，false表示失败
     */
    fun enableBluetooth(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return bluetoothAdapter.enable()
        }
        return true
    }

    /**
     * 关闭蓝牙
     *
     * @return true表示成功，false表示失败
     */
    fun disableBluetooth(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return bluetoothAdapter.disable()
        }
        return true
    }

    val enableBluetoothIntent: Intent
        /**
         * 请求开启蓝牙（需要用户确认）
         *
         * @return Intent对象，需要在Activity中使用startActivityForResult启动
         */
        get() = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

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
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                return null
            }
            try {
                return bluetoothAdapter.getBondedDevices()
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
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            if (callback != null) {
                callback.onScanFailed("蓝牙未开启或不可用")
            }
            return false
        }

        if (!hasBluetoothPermission()) {
            if (callback != null) {
                callback.onScanFailed("缺少蓝牙权限")
            }
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
            return bluetoothAdapter.startDiscovery()
        } catch (e: SecurityException) {
            e.printStackTrace()
            if (callback != null) {
                callback.onScanFailed("权限不足: " + e.message)
            }
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
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            try {
                bluetoothAdapter.cancelDiscovery()
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
            return bluetoothAdapter != null && bluetoothAdapter.isDiscovering()
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
                return bluetoothAdapter.getName()
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
            return bluetoothAdapter.setName(name)
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
        get() {
            if (bluetoothAdapter == null) {
                return null
            }
            try {
                return bluetoothAdapter.getAddress()
            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
        }

    /**
     * 释放资源
     */
    fun release() {
        stopScan()
    }
}
