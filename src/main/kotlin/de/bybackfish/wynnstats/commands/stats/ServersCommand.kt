package de.bybackfish.wynnstats.commands.stats

import de.bybackfish.wynnstats.WynnMod
import de.bybackfish.wynnstats.commands.Kommand
import net.minecraft.client.Minecraft
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class ServersCommand: Kommand(k@{

    val servers = WynnMod.stats.getServers()!!
    if (it.isEmpty()) {
        send(TextComponentString("§6--- §7${servers.servers.size} §6Servers with §7${servers.servers.values.sumOf { it.size }} §6Users §6---"))
        send(TextComponentString(""))
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
                send(component)
            }
        send(TextComponentString("§6-------------"))
        return@k
    }

    val serverName = it[0]
    val server = servers.servers[serverName]
        ?: return@k send(TextComponentString("§cServer not found!"))

    send(TextComponentString("§6--- §6Server §7${serverName} §6has §7${server.size} §6Players online ---"))
    send(TextComponentString(""))
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
        send(memberName)
    }
    send(TextComponentString("§6-------------"))

    return@k

}, "servers")


