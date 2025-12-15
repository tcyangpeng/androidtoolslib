package com.tcyp.myutils.net

class ApiException (val errorCode: Int, override val message: String?) : RuntimeException(message) {
    companion object {
        const val UNKNOWN = -1000
        const val PARSE_ERROR = -1001
        const val NETWORK_ERROR = -1002
        const val SERVER_ERROR = -1003
    }
}