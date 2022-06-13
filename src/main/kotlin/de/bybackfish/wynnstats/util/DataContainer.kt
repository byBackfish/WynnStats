package de.bybackfish.wynnstats.util

import net.minecraft.client.Minecraft

class DataContainer {
    private val data = mutableMapOf<String, Any>()

    fun setContainer(key: String, value: Any) {
        data[key] = value
    }

    fun <T> getOrDefaultContainer(key: String, default: T): T {
        val data = data[key]
        if(data == null) {
            Minecraft.getMinecraft().player.sendMessage(
                net.minecraft.util.text.TextComponentString("No data found for key: $key")
            )
            return default
        }
        return data as T
    }

    fun <T> getContainer(key: String): T {
        return data[key] as T
    }
}
