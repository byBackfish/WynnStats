package de.bybackfish.wynnstats.util

data class UpdateContainer(var lastUpdate: Long = 0, val updateInterval: Long = 30000, var data: Any? = null) {
    fun isUpdateNeeded(): Boolean {
        return System.currentTimeMillis() - lastUpdate > updateInterval
    }
}
