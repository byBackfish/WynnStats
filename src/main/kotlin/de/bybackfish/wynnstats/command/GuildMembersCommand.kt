package de.bybackfish.wynnstats.command

import de.bybackfish.wynnstats.WynnStatsMod.stats
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class GuildMembersCommand : CommandBase() {

    override fun getName(): String = "guildmembers"
    override fun getAliases(): MutableList<String> =
        listOf("gsmembers", "guildsmembers", "viewguildmembers", "guildmemberlist").toMutableList()

    override fun getRequiredPermissionLevel(): Int = 0
    override fun getUsage(sender: ICommandSender): String = "/guildmembers <guild>"

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

                val members = TextComponentString("§6- Members: §7${guild.members.size}")
                sender.sendMessage(members)
                sender.sendMessage(TextComponentString(""))

                for (member in guild.members.sortedBy { it.contributed }.reversed()) {
                    val memberName =
                        TextComponentString("§6- §7${member.name} ${if (member.rank == "OWNER") "§e⭐" else if (member.name == Minecraft.getMinecraft().player.name) "§b⭐" else ""}")
                    memberName.style = Style().setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            TextComponentString("§6Rank: §7${member.rank}\n§6Contributed: §7${member.contributed}\n§8Click to view profile")
                        )
                    )
                        .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/player ${member.name}"))
                    sender.sendMessage(memberName)
                }

                sender.sendMessage(TextComponentString("§6-------------"))
            } catch (e: Exception) {
                e.printStackTrace()
                sender.sendMessage(TextComponentString("§cError: §7${e.message}"))
            }
        }.start()
    }


}