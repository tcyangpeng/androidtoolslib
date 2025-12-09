# ft-android-lib
项目常用工具类集合

## 工具类说明

### 1. DateUtils - 日期转换工具类
位置: `com.focustech.android.lib.util.DateUtils`

提供日期格式化、转换、计算等常用功能：
- 多种常用日期格式常量（`FORMAT_YYYY_MM_DD`, `FORMAT_YYYY_MM_DD_HH_MM_SS` 等）
- 日期格式化和解析方法
- 日期算术运算（增加/减少天数、月份、年份）
- 日期比较和验证（判断今天、昨天、闰年等）
- 获取月份的第一天和最后一天
- 计算日期间隔

**示例：**
```java
// 获取当前时间的格式化字符串
String now = DateUtils.getCurrentDateString();

// 格式化日期
String formatted = DateUtils.formatDate(new Date(), DateUtils.FORMAT_YYYY_MM_DD);

// 日期算术
Date tomorrow = DateUtils.addDays(new Date(), 1);

// 日期解析
Date date = DateUtils.parseDate("2024-11-20", DateUtils.FORMAT_YYYY_MM_DD);
```

### 2. BluetoothManager - 蓝牙管理工具类
位置: `com.focustech.android.lib.util.BluetoothManager`

提供蓝牙开关、扫描、配对等功能：
- 检查设备蓝牙支持和状态
- 开启/关闭蓝牙
- 扫描蓝牙设备（带回调接口）
- 获取已配对设备列表
- 权限检查
- 设备可发现管理

**示例：**
```java
BluetoothManager btManager = new BluetoothManager(context);

// 检查蓝牙是否支持
if (btManager.isBluetoothSupported()) {
    // 开启蓝牙
    btManager.enableBluetooth();
    
    // 扫描设备
    btManager.startScan(new BluetoothManager.BluetoothScanCallback() {
        @Override
        public void onDeviceFound(BluetoothDevice device) {
            // 发现设备
        }
        
        @Override
        public void onScanFinished() {
            // 扫描完成
        }
        
        @Override
        public void onScanFailed(String errorMsg) {
            // 扫描失败
        }
    });
}
```

### 3. PermissionManager - 权限申请管理工具类
位置: `com.focustech.android.lib.util.PermissionManager`

用于处理Android 6.0及以上版本的运行时权限申请：
- 检查单个或多个权限
- 请求权限
- 获取未授予的权限列表
- 处理权限申请结果（带回调接口）
- 检查是否应该显示权限申请理由
- 权限友好名称映射

**示例：**
```java
// 检查权限
if (PermissionManager.hasPermission(context, Manifest.permission.CAMERA)) {
    // 有相机权限
}

// 请求权限
String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
PermissionManager.requestPermissions(activity, permissions, REQUEST_CODE);

// 在onRequestPermissionsResult中处理结果
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    PermissionManager.handlePermissionResult(this, permissions, grantResults, 
        new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // 权限已授予
            }
            
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 权限被拒绝
            }
            
            @Override
            public void onPermissionPermanentlyDenied(List<String> permanentlyDeniedPermissions) {
                // 权限被永久拒绝
            }
        });
}
```

### 4. ListUtils - 列表工具类
位置: `com.focustech.android.lib.util.ListUtils`

提供列表操作的常用方法：
- 安全的列表访问（防止越界）
- 列表创建和转换
- 排序和反转
- 去重
- 列表合并和分割
- 过滤操作
- 元素交换

**示例：**
```java
// 安全获取列表元素
String first = ListUtils.getFirst(list);
String last = ListUtils.getLast(list);

// 去重
List<String> uniqueList = ListUtils.removeDuplicates(list);

// 合并列表
List<String> merged = ListUtils.merge(list1, list2, list3);

// 分割列表
List<List<String>> partitions = ListUtils.partition(list, 10);

// 过滤null元素
List<String> filtered = ListUtils.filterNull(list);
```

### 5. MathUtils - 数学工具类
位置: `com.focustech.android.lib.util.MathUtils`

提供数学计算、随机数生成等功能：
- 随机数生成（int, long, float, double, boolean）
- 随机字符串生成
- 精确的浮点数运算（加减乘除）
- 最大值、最小值、绝对值
- 四舍五入、向上取整、向下取整
- 数值限制（clamp）
- 数学函数（平方、平方根、幂次方）

**示例：**
```java
// 生成随机数
int random = MathUtils.randomInt(1, 100);  // 1到99的随机整数
String randomStr = MathUtils.randomString(10);  // 10位随机字符串

// 精确计算
double result = MathUtils.add(0.1, 0.2);  // 避免浮点数精度问题
double divided = MathUtils.divide(10.0, 3.0, 2);  // 保留2位小数

// 数值限制
int clamped = MathUtils.clamp(value, 0, 100);  // 限制在0-100之间
```

### 6. AppBaseActivity - Activity基类
位置: `com.focustech.android.lib.page.AppBaseActivity`

基于MVVM的Activity基类，集成了DataBinding支持。

### 7. AppBaseFragment - Fragment基类
位置: `com.focustech.android.lib.page.AppBaseFragment`

基于MVVM的Fragment基类，集成了DataBinding支持。

## 其他已有工具类

- **GeneralUtils**: 通用工具类，包含null判断、字符串判断、日期获取等
- **NetworkUtil**: 网络工具类
- **Device**: 设备信息工具类
- **CrashHandler**: 崩溃处理工具类

## 使用说明

1. 将本库作为依赖添加到项目中
2. 导入相应的工具类
3. 根据需要调用工具类的静态方法或创建实例使用

## 许可证

本项目为开源项目，具体许可证信息请参考项目设置。
