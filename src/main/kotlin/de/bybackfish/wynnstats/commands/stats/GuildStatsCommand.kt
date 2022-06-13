package de.bybackfish.wynnstats.commands.stats

import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.WynnMod
import de.bybackfish.wynnstats.commands.Kommand
import de.bybackfish.wynnstats.util.UpdateContainer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class GuildStatsCommand : Kommand(k@{

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

    val members = TextComponentString("§6- Members: §7${guild.members.size} §8(Hover)")
    members.style = Style().setHoverEvent(
        HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            TextComponentString("§6Members: §7${guild.members.size}\n§8Click to view list of members.")
        )
    )
        .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guildmembers ${guild.name}"))
    send(members)
    send(TextComponentString(""))

    val om = TextComponentString("§6- Online Members §8(Click)")
    om.style = Style().setClickEvent(
        ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/onlinemembers ${guild.name}"
        )
    )
    send(om)
    send(TextComponentString(""))

    val territories = TextComponentString("§6- Territories: §7${guild.territories}")
    send(territories)



    send(TextComponentString(""))
    send(TextComponentString("§6- Created at: §7${guild.created}"))
    send(TextComponentString("§6-------------"))

}, "guildstats", alias = arrayOf("guildstat", "gstat", "gv", "gs"), autoComplete = autocomplete@{ args ->
    val container: UpdateContainer =
        WynnMod.data.getOrDefaultContainer("guildstatsUpdate", UpdateContainer(data = arrayListOf<String>()))
    if (container.isUpdateNeeded()) {
        Thread {
            container.lastUpdate = System.currentTimeMillis()
            container.data = WynnStats().getGuilds()!!.guilds

            WynnMod.data.setContainer("guildstatsUpdate", container)
        }.start()
    }
    val guilds = container.data as List<String>
    if (args.isEmpty()) return@autocomplete mutableListOf()
    val name = StringBuilder(args[0])
    for (arg in args.slice(1 until args.size)) {
        name.append(" ").append(arg)
    }
    val possible = guilds.filter { guild -> guild.startsWith(name.toString()) }.map { guild ->
        guild.split(" ")[args.size - 1]
    }.toMutableList()
    if (possible.size < 30) {
        return@autocomplete possible
    }
    return@autocomplete mutableListOf()
})
