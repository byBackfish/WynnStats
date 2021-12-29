package de.bybackfish.wynnstats


import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.command.*
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(
    modid = WynnStatsMod.MOD_ID,
    name = WynnStatsMod.MOD_NAME,
    version = WynnStatsMod.VERSION,
    modLanguageAdapter = "de.bybackfish.wynnstats.adapter.Adapter"
)
object WynnStatsMod {
    const val MOD_ID = "wynnstats"
    const val MOD_NAME = "WynnStats Mod"
    const val VERSION = "1.0-beta"
    val stats = WynnStats()

    @JvmStatic
    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent) {
        val commandHandler = ClientCommandHandler.instance
        commandHandler.registerCommand(PlayerCommand())
        commandHandler.registerCommand(GuildCommand())
        commandHandler.registerCommand(GuildMembersCommand())
        commandHandler.registerCommand(ServersCommand())
        commandHandler.registerCommand(ItemCommand())

    }


}