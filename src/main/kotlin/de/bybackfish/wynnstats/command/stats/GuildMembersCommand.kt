package de.bybackfish.wynnstats.command.stats

import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.WynnMod
import de.bybackfish.wynnstats.commands.Kommand
import net.minecraft.client.Minecraft
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class GuildMembersCommand: Kommand(k@{

    if (it.isEmpty()) {
        send(TextComponentString("§cPlease specify a guild name!"))
        return@k
    }
    val name = it.joinToString(" ")
    val guild = WynnMod.stats.getGuild(name)
        ?: return@k send(TextComponentString("§7The specified guild does not exist."))

    send(TextComponentString("§6--- Guild §7[${guild.prefix}] ${guild.name} §6---"))
    val level = TextComponentString("§6- Level: §7${guild.level} ")
    level.style = Style().setHoverEvent(
        HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            TextComponentString("§6LeveL: §7${guild.level}\n§6Experience: §7${guild.xp}")
        )
    )
    send(level)
    send(TextComponentString(""))

    val members = TextComponentString("§6- Members: §7${guild.members.size}")
    send(members)
    send(TextComponentString(""))

    for (member in guild.members.sortedBy { it.contributed }.reversed()) {
        val memberName =
            TextComponentString("§6- §7${member.name} ${if (member.rank == "OWNER") "§e⭐" else if (member.name == Minecraft.getMinecraft().player.name) "§b⭐" else ""}")
        memberName.style = Style().setHoverEvent(
            HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponentString("§6Rank: §7${member.rank}\n§6Contributed: §7${member.contributed}\n§8Click to view profile")
            )
        )
            .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playerstats ${member.name}"))
        send(memberName)
    }

    send(TextComponentString("§6-------------"))

}, "guildmembers") {
}
