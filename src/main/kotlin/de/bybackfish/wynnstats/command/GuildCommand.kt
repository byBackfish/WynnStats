package de.bybackfish.wynnstats.command

import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnstats.WynnStatsMod.stats
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class GuildCommand : CommandBase() {

    var guilds = ArrayList<String>()
    var lastUpdate: Long = 0
    val updateTimer = 300000

    init {
        lastUpdate = System.currentTimeMillis();
        guilds = stats.getGuilds()!!.guilds
    }

    override fun getName(): String = "gs"
    override fun getAliases(): MutableList<String> =
        listOf("guildstats", "guilds", "viewguild", "guildviewer").toMutableList()

    override fun getRequiredPermissionLevel(): Int = 0
    override fun getUsage(sender: ICommandSender): String = "/gs <guild>"

    override fun getTabCompletions(
        server: MinecraftServer?,
        sender: ICommandSender?,
        args: Array<String>,
        targetPos: BlockPos?
    ): MutableList<String> {
        if (lastUpdate + updateTimer < System.currentTimeMillis()) {
            Thread {
                lastUpdate = System.currentTimeMillis()
                guilds = WynnStats().getGuilds()!!.guilds
            }.start()
        }
        if (args.isEmpty()) return mutableListOf()
        val name = StringBuilder(args[0])
        for (arg in args.slice(1 until args.size)) {
            name.append(" ").append(arg)
        }
        val possible = guilds.filter { it.startsWith(name.toString()) }.map {
            it.split(" ")[args.size - 1]
        }.toMutableList()
        if (possible.size < 30) {
            return possible
        }
        return mutableListOf()
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        if (args == null || sender == null) {
            return
        }
        if (args.isEmpty()) {
            sender.sendMessage(TextComponentString("§cPlease specify a guild name!"))
            return
        }
        val name = args.joinToString(" ")

        Thread {
            try {
                val guild = stats.getGuild(name)
                    ?: return@Thread sender.sendMessage(TextComponentString("§7The specified guild does not exist."))

                sender.sendMessage(TextComponentString("§6--- Guild §7[${guild.prefix}] ${guild.name} §6---"))
                val level = TextComponentString("§6- Level: §7${guild.level} ")
                level.style = Style().setHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponentString("§6LeveL: §7${guild.level}\n§6Experience: §7${guild.xp}")
                    )
                )
                sender.sendMessage(level)
                sender.sendMessage(TextComponentString(""))

                val members = TextComponentString("§6- Members: §7${guild.members.size} §8(Hover)")
                members.style = Style().setHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponentString("§6Members: §7${guild.members.size}\n§8Click to view list of members.")
                    )
                )
                    .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guildmembers ${guild.name}"))
                sender.sendMessage(members)
                sender.sendMessage(TextComponentString(""))

                val territories = TextComponentString("§6- Territories: §7${guild.territories}")
                sender.sendMessage(territories)

                sender.sendMessage(TextComponentString(""))
                sender.sendMessage(TextComponentString("§6- Created at: §7${guild.created}"))
                sender.sendMessage(TextComponentString("§6-------------"))

            } catch (e: Exception) {
                e.printStackTrace()
                sender.sendMessage(TextComponentString("§cError: §7${e.message}"))
            }
        }.start()
    }


}