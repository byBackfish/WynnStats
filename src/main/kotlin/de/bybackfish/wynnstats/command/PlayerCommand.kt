package de.bybackfish.wynnstats.command

import camelCase
import de.bybackfish.wynnstats.WynnStatsMod.stats
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class PlayerCommand : CommandBase() {

    override fun getName(): String = "player"
    override fun getAliases(): MutableList<String> = listOf("playerviewer", "pv", "playerstats", "ps").toMutableList()
    override fun getRequiredPermissionLevel(): Int = 0
    override fun getUsage(sender: ICommandSender): String = "/player <player>"

    override fun getTabCompletions(
        server: MinecraftServer?,
        sender: ICommandSender?,
        args: Array<out String>?,
        targetPos: BlockPos?
    ): MutableList<String> =
        if (server == null || sender == null) mutableListOf()
        else if (args == null || args.isEmpty()) server.onlinePlayerNames.toMutableList()
        else server.onlinePlayerNames.filter { it.startsWith(args[0], true) }.toMutableList()

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        if (args == null || sender == null) {
            println("None")
            return
        }
        Thread {
            try {
                val name: String = if (args.isEmpty()) sender.name else args[0]
                println("Name: $name")
                val player = stats.getPlayer(name)
                    ?: return@Thread sender.sendMessage(TextComponentString("§7The specified player does not exist."))
                if (args.size < 2) {
                    val data = player.data[0]
                    sender.sendMessage(TextComponentString("§6--- Player §7[${data.rank}] ${data.username} §6---"))
                    sender.sendMessage(TextComponentString("§7You need to specify a class to see the stats. Just click on it!"))
                    for (playerClasses in data.classes) {
                        val component = TextComponentString(
                            "§6${
                                playerClasses.name?.camelCase()
                            } §7[${playerClasses.level}]"
                        )
                        component.style = Style().setHoverEvent(
                            HoverEvent(
                                (HoverEvent.Action.SHOW_TEXT), TextComponentString(
                                    "§7Combat: §b${playerClasses.professions.combat.level}\n"
                                            + "§7Playtime: §b${playerClasses.playtime}min\n"
                                )
                            )
                        ).setClickEvent(
                            ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/player ${data.username} ${playerClasses.name}"
                            )
                        )
                        sender.sendMessage(component)
                    }
                    sender.sendMessage(TextComponentString("§6-------------"))
                    return@Thread
                }
                val clazz = args[1]
                val playerClass = player.data[0].classes.firstOrNull { it.name == clazz }
                    ?: return@Thread sender.sendMessage(TextComponentString("§7The specified class does not exist."))
                sender.sendMessage(TextComponentString("§6--- Player §7[${player.data[0].meta.tag!!.value}] ${player.data[0].username} §6---"))
                sender.sendMessage(TextComponentString("§6- Class: §7${clazz.camelCase()} "))
                sender.sendMessage(TextComponentString(""))

                if (player.data[0].meta.location.online) {
                    sender.sendMessage(TextComponentString("§6- Online: §6[§7${player.data[0].meta.location}m§6] §a🟢"))
                    sender.sendMessage(TextComponentString(""))
                }

                val professions = TextComponentString("§6- Professions §8(Hover) ")
                professions.style = Style()
                    .setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, TextComponentString(
                                "§6Combat: §7${playerClass.professions.combat.level}\n" +
                                        "§6Farming: §7${playerClass.professions.farming.level}\n" +
                                        "§6Mining: §7${playerClass.professions.mining.level}\n" +
                                        "§6Foraging: §7${playerClass.professions.woodcutting.level}\n" +
                                        "§6Fishing: §7${playerClass.professions.fishing.level}\n" +
                                        "\n" +
                                        "§6Cooking: §7${playerClass.professions.cooking.level}\n" +
                                        "§6Alchemism: §7${playerClass.professions.alchemism.level}\n" +
                                        "§6Armouring: §7${playerClass.professions.armouring.level}\n" +
                                        "§6Jeweling: §7${playerClass.professions.jeweling.level}\n" +
                                        "§6Scribing: §7${playerClass.professions.scribing.level}\n" +
                                        "§6Tailoring: §7${playerClass.professions.tailoring.level}\n" +
                                        "§6Weaponsmithing: §7${playerClass.professions.weaponsmithing.level}\n" +
                                        "§6Woodworking: §7${playerClass.professions.woodworking.level}\n"

                            )
                        )
                    )

                sender.sendMessage(professions)
                sender.sendMessage(TextComponentString(""))

                val guild = TextComponentString("§6- §6Guild: §7${player.data[0].guild.name} §8(Hover) ")
                guild.style = Style()
                    .setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, TextComponentString(
                                "§7Rank: §6${player.data[0].guild.rank}\n§8Click to view guild information"
                            )
                        )
                    ).setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gs ${player.data[0].guild.name}"))



                sender.sendMessage(guild)
                sender.sendMessage(TextComponentString(""))

                val other = TextComponentString("§6- Other §8(Hover) ")
                other.style = Style()
                    .setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, TextComponentString(
                                "§6Quests completed: ${playerClass.quests.list.size}\n" +
                                        "§6Kills: §7${playerClass.mobsKilled}\n" +
                                        "§6Deaths: §7${playerClass.deaths}\n" +
                                        "§6Chests found: §7${playerClass.chestsFound}\n" +
                                        "§6Discoveries: §7${playerClass.discoveries}\n" +
                                        "§6Logins: §7${playerClass.logins}\n"
                            )
                        )
                    )

                sender.sendMessage(other)
                sender.sendMessage(TextComponentString(""))

                val dungeons = TextComponentString("§6- §6Dungeons §8(Hover) ")

                dungeons.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString(
                    "§6Dungeons completed: \n§6Unique: §7${playerClass.dungeons.list.size} §7| §6Total: §7${playerClass.dungeons.list.sumOf { it.completed }}\n" +
                            playerClass.dungeons.list.joinToString("\n") {
                                "§6${it.name!!.camelCase()}: §7${it.completed}"
                            }
                )))

                sender.sendMessage(dungeons)
                sender.sendMessage(TextComponentString(""))
                val raids = TextComponentString("§6- §6Raids §8(Hover)  ")

                raids.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString(
                    "Raids completed: \n§6Unique: §7${playerClass.raids.list.size} §7 | §6Total: §7${playerClass.raids.completed}\n" +
                            playerClass.raids.list.joinToString("\n") {
                                "§6${it.name!!.camelCase()}: §7${it.completed}"
                            }
                )))


                sender.sendMessage(raids)
                sender.sendMessage(TextComponentString(""))
                sender.sendMessage(TextComponentString("§6- Playtime (Total): §7${player.data[0].meta.playtime}min"))
                sender.sendMessage(TextComponentString("§6- Playtime (Class): §7${playerClass.playtime}min"))
                sender.sendMessage(TextComponentString("§6-------------"))
            } catch (e: Exception) {
                e.printStackTrace()
                sender.sendMessage(TextComponentString("§cError: §7${e.message}"))
            }
        }.start()
    }
}