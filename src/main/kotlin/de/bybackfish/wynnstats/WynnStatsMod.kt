package de.bybackfish.wynnstats


import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.command.GuildCommand
import de.bybackfish.wynnstats.command.GuildMembersCommand
import de.bybackfish.wynnstats.command.PlayerCommand
import de.bybackfish.wynnstats.command.ServersCommand
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
    const val MOD_ID = "minecraft-forge-kotlin-template"
    const val MOD_NAME = "Minecraft Forge Kotlin Template"
    const val VERSION = "2019.1-1.2.23"
    val stats = WynnStats()

    @JvmStatic
    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent) {
        println("LOADING you FISH")

        //register a command in forge
        val commandHandler = ClientCommandHandler.instance
        commandHandler.registerCommand(PlayerCommand())
        commandHandler.registerCommand(GuildCommand())
        commandHandler.registerCommand(GuildMembersCommand())
        commandHandler.registerCommand(ServersCommand())

    }


}