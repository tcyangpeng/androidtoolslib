package com.tcyp.myutils.databus

/**
 * 登录事件,这边仅作功能验证，实际项目请根据业务逻辑进行扩展
 */
data class LoginEvent(var loginSuccess: Boolean): AppEvent {
}

data class LogoutEvent(var logoutSuccess: Boolean): AppEvent {
}