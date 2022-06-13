package de.bybackfish.wynnstats


import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.commands.stats.GuildMembersCommand
import de.bybackfish.wynnstats.commands.stats.GuildStatsCommand
import de.bybackfish.wynnstats.commands.stats.PlayerStatsCommand
import de.bybackfish.wynnstats.commands.stats.ServersCommand
import de.bybackfish.wynnstats.util.DataContainer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.entity.RenderEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.management.PlayerChunkMapEntry
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Mod(
    modid = WynnMod.MOD_ID,
    name = WynnMod.MOD_NAME,
    version = WynnMod.VERSION,
    modLanguageAdapter = "de.bybackfish.wynnstats.adapter.Adapter"
)
object WynnMod {
    const val MOD_ID = "wynnstats"
    const val MOD_NAME = "WynnStats Mod"
    const val VERSION = "1.0-beta"
    val stats = WynnStats()

    var data: DataContainer = DataContainer()

    @JvmStatic
    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent) {
        val commandHandler = ClientCommandHandler.instance
       PlayerStatsCommand()
        GuildStatsCommand()
        GuildMembersCommand()
        ServersCommand()


        MinecraftForge.EVENT_BUS.register(this)

    }


}