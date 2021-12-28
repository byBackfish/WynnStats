package de.bybackfish.wynnstats.command

import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnapi.network.ServerList
import de.bybackfish.wynnstats.WynnStatsMod.stats
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class ServersCommand : CommandBase() {

    var servers = ServerList()
    var lastUpdate: Long = 0
    val updateTimer = 300000

    override fun getName(): String = "servers"
    override fun getAliases(): MutableList<String> =
        listOf("serverlist", "serverslist", "listservers", "sl").toMutableList()

    override fun getRequiredPermissionLevel(): Int = 0
    override fun getUsage(sender: ICommandSender): String = "/serverlist [guild]"

    override fun getTabCompletions(
        server: MinecraftServer?,
        sender: ICommandSender,
        args: Array<out String>,
        targetPos: BlockPos?
    ): MutableList<String> {

        if (lastUpdate + updateTimer < System.currentTimeMillis()) {
            Thread {
                lastUpdate = System.currentTimeMillis()
                servers = WynnStats().getServers()!!
                println("Thread done!°")
            }.start()
        }

        if (args.size == 1) {
            return servers.servers.filter { it.key.startsWith(args[0]) }.map { it.key }.toMutableList()
        }
        return mutableListOf()
    }

    override fun execute(mc: MinecraftServer?, sender: ICommandSender, args: Array<out String>) {
        Thread {
            servers = stats.getServers()!!
            if (args.isEmpty()) {
                sender.sendMessage(TextComponentString("§6--- §7${servers.servers.size} §6Servers with §7${servers.servers.values.sumOf { it.size }} §6Users §6---"))
                sender.sendMessage(TextComponentString(""))
                servers.servers.toList().sortedBy { (_, value) -> value.size }.toMap()
                    .forEach {
                        val component = TextComponentString("§6${it.key}: §7${it.value.size}")
                        component.style = Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                TextComponentString("§6${it.key}: §7${it.value.size}\n§8Click to show player list")
                            )
                        )
                            .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/servers ${it.key}"))
                        sender.sendMessage(component)
                    }
                sender.sendMessage(TextComponentString("§6-------------"))
                return@Thread
            }

            val serverName = args[0]
            val server = servers.servers[serverName]
                ?: return@Thread sender.sendMessage(TextComponentString("§cServer not found!"))

            sender.sendMessage(TextComponentString("§6--- §6Server §7${serverName} §6has §7${server.size} §6Players online ---"))
            sender.sendMessage(TextComponentString(""))
            server.sortedBy { it.length }.forEach {
                val memberName =
                    TextComponentString("§6- §7${it} ${if (it == Minecraft.getMinecraft().player.name) "§b⭐" else ""}")
                memberName.style = Style().setHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponentString("§8Click to view profile")
                    )
                )
                    .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/player $it"))
                sender.sendMessage(memberName)
            }
            sender.sendMessage(TextComponentString("§6-------------"))

            return@Thread

        }.start()
    }


}