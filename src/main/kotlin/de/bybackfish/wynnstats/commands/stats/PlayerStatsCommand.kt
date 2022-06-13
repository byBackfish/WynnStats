package de.bybackfish.wynnstats.commands.stats

import camelCase
import com.mojang.realmsclient.gui.ChatFormatting
import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnapi.player.classes.PlayerClasses
import de.bybackfish.wynnstats.WynnMod
import de.bybackfish.wynnstats.commands.Kommand
import net.minecraft.client.Minecraft
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

class PlayerStatsCommand : Kommand(k@{ args ->

    val playerName = if (args.isEmpty()) player.name else args[0]
    val player = WynnMod.stats.getPlayer(playerName) ?: return@k player.sendMessage(TextComponentString("§7The specified player does not exist."))

    val data = player.getData()

    if (args.size == 1) {

        send("§6--- Player §7[${if(data.rank == "Player") data.meta.tag?.value ?: "None" else data.rank }] ${data.username} §6---")
        send("§7Click on a class to get more info!")

        send("")

        if (player.data[0].meta.location.online) {
            send(TextComponentString("§7- §aOnline: §6[§7${player.data[0].meta.location.server}§6]"))
        } else {
            send(TextComponentString("§7- §cOffline"))
        }


        send(" ")

        for (clazz in data.classes) {
            send(
                createComponent(
                    content = "§7- §6${clazz.name!!.camelCase()} §8[§7${clazz.level}§8] ${buildGamemodeString(clazz)}",
                    hoverEvent = HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, TextComponentString(
                            "§7Click to get more data about this class!\n\n" +
                                "§7Combat: §b${clazz.professions.combat.level}\n"
                                + "§7Playtime: §b${clazz.playtime}min\n"
                        )
                    ),
                    clickEvent = ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/playerstats ${data.username} ${clazz.name}"
                    )
                )
            )
        }


        send("")

        send(
            createComponent(
                content = "§7- §6Global §8(Hover)",
                hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    TextComponentString(
                        "§7Total Level: §b${data.global.totalLevel.combined}\n§7Chests found: §b${data.global.chestsFound}\n§7Playtime: §b${data.meta.playtime}min"
                    ),
                )
            )
        )

        send("§6-------------")
        return@k
    }


    val className = args[1]
    val playerClass = player.data[0].classes.firstOrNull { it.name == className }
        ?: return@k send(TextComponentString("§7The specified class does not exist."))
    send(TextComponentString("§6--- Player §7[${player.data[0].meta.tag!!.value}] ${player.data[0].username} §6---"))
    send(TextComponentString("§6- Class: §7${className.camelCase()} "))
    send(TextComponentString(""))

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

    send(professions)
    send(TextComponentString(""))

    val guild = TextComponentString("§6- §6Guild: §7${player.data[0].guild.name} §8(Hover) ")
    guild.style = Style()
        .setHoverEvent(
            HoverEvent(
                HoverEvent.Action.SHOW_TEXT, TextComponentString(
                    "§7Rank: §6${player.data[0].guild.rank}\n§8Click to view guild information"
                )
            )
        ).setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gs ${player.data[0].guild.name}"))


    send(guild)
    send("")

    val component = TextComponentString("§7- §6Skills: ${buildSkillString(playerClass)}");
    send(component)


    send(TextComponentString(""))

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

    send(other)
    send(TextComponentString(""))

    val dungeons = TextComponentString("§6- §6Dungeons §8(Hover) ")

    dungeons.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString(
        "§6Dungeons completed: \n§6Unique: §7${playerClass.dungeons.list.size} §7| §6Total: §7${playerClass.dungeons.list.sumOf { it.completed }}\n" +
            playerClass.dungeons.list.joinToString("\n") {
                "§6${it.name!!.camelCase()}: §7${it.completed}"
            }
    )))

    send(dungeons)
    send(TextComponentString(""))
    val raids = TextComponentString("§6- §6Raids §8(Hover)  ")

    raids.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString(
        "Raids completed: \n§6Unique: §7${playerClass.raids.list.size} §7 | §6Total: §7${playerClass.raids.completed}\n" +
            playerClass.raids.list.joinToString("\n") {
                "§6${it.name!!.camelCase()}: §7${it.completed}"
            }
    )))


    send(raids)
    send(TextComponentString(""))
    send(TextComponentString("§6- Playtime (Class): §7${playerClass.playtime}min"))
    send(TextComponentString("§6-------------"))





}, "playerstats", alias = arrayOf("pv"))

fun buildSkillString(clazz: PlayerClasses): String {
    var output = "";
    output += ChatFormatting.DARK_GREEN.toString() + clazz.skills.strength + "  "
    output += ChatFormatting.YELLOW.toString() + clazz.skills.dexterity + "  "
    output += ChatFormatting.AQUA.toString() + clazz.skills.intelligence + "  "
    output += ChatFormatting.RED.toString() + clazz.skills.defence + "  "
    output += ChatFormatting.WHITE.toString() + clazz.skills.agility

    return output;
}

fun buildGamemodeString(clazz: PlayerClasses): String{
    var output = "";

    if(clazz.gamemode.hardcore) output+= GamemodeIcons.HARDCORE;
    if(clazz.gamemode.ironman) output+= GamemodeIcons.IRONMAN;
    if(clazz.gamemode.craftsman) output+= GamemodeIcons.CRAFTSMAN;
    if(clazz.gamemode.hunted) output+= GamemodeIcons.HUNTED;

    return output;
}


object GamemodeIcons {
    val HARDCORE = ChatFormatting.RED.toString() + "☠"
    val IRONMAN = ChatFormatting.GOLD.toString() + "❂"
    val CRAFTSMAN = ChatFormatting.DARK_AQUA.toString() + "✿"
    val HUNTED = ChatFormatting.DARK_PURPLE.toString() + "⚔"
}