package de.bybackfish.wynnstats.command

import com.mojang.realmsclient.gui.ChatFormatting
import containsIgnoreCase
import de.bybackfish.wynnapi.WynnStats
import de.bybackfish.wynnapi.items.Item
import de.bybackfish.wynnapi.items.ItemCategory
import de.bybackfish.wynnapi.items.Items
import de.bybackfish.wynnstats.WynnStatsMod
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

import de.bybackfish.wynnstats.WynnStatsMod.stats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import java.awt.Desktop
import java.net.URI
import javax.swing.DesktopManager


class ItemCommand : CommandBase() {

    var items: List<Item>? = null
    var lastUpdate: Long = 0
    val updateTimer = 300000

    val PER_PAGE = 15
    

    init {
        lastUpdate = System.currentTimeMillis();
        items = stats.getItemsByCategory(ItemCategory.ALL)!!.items
        items = (items as ArrayList<Item>).sortedBy { getTier(it.tier) }
    }

    fun getTier(tier: String?): String {
        return when (tier) {
            "normal" -> "G"
            "unique" -> "F"
            "rare" -> "E"
            "set" -> "D"
            "legendary" -> "C"
            "fabled" -> "B"
            "mythic" -> "A"
            else -> "Z"
        }
    }

    override fun getName(): String = "items"
    override fun getAliases(): MutableList<String> =
        listOf("itemlist", "viewitems").toMutableList()

    override fun getRequiredPermissionLevel(): Int = 0
    override fun getUsage(sender: ICommandSender): String = "/items"

    override fun getTabCompletions(
        server: MinecraftServer?,
        sender: ICommandSender,
        args: Array<out String>,
        targetPos: BlockPos?
    ): MutableList<String> {
        if (lastUpdate + updateTimer < System.currentTimeMillis()) {
            Thread {
                lastUpdate = System.currentTimeMillis()
                items = stats.getItemsByCategory(ItemCategory.ALL)!!.items
                items = (items as ArrayList<Item>).sortedBy { getTier(it.tier) }
            }.start()
        }
        println("Args: ${args.size} | ${args[0]}")
        if (args.isEmpty() || args[0].isBlank()) return mutableListOf("name", "category", "all")

        if(args[0] == "name") {
            val name = StringBuilder(args[1])
            for (arg in args.slice(2 until args.size)) {
                name.append(" ").append(arg)
            }
            println("Query: $name")
            val possible = items!!.filter { it.name!!.startsWith(name.toString()) }.map {
               try{
                   it.name!!.split(" ")[args.size - 1]
               }catch (e: IndexOutOfBoundsException) {
                it.name!!
               }

            }.toMutableList()
            if (possible.size < 30) {
                return possible
            }
        }

        if(args[0] == "category")
            return ItemCategory.values().filter { it.name.startsWith(args[1]) }.map { it.name }.toMutableList()

        return mutableListOf()
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender, args: Array<out String>) {
        println(items!!.size)
        Thread {
            try{
                if(args.isEmpty()) return@Thread sender.sendMessage(TextComponentString("/items <name|category|all> [name]"))


        if(args[0] == "name") {
            val name = StringBuilder(args[1])
            for (arg in args.slice(2 until args.size)) {
                name.append(" ").append(arg)
            }
            val possible = items!!.filter { it.name!!.lowercase().startsWith(name.toString().lowercase()) }
          return@Thread showItems(sender, possible, 0, "name $name")
        }

        if(args[0] == "category"){
            var num = 0
            if(args.size == 3){
                num = args[2].toInt()
            }
            val name = args[1]
            val possible = items!!.filter { (if(it.type == null) "None" else it.type)!!.lowercase() == (name.lowercase()) }
            return@Thread showItems(sender, possible, num, "category $name")
        }

        if(args[0] == "all") {
            var num = 0
            if(args.size == 2){
                num = args[1].toInt()
            }
            val possible = items!!
            showItems(sender, possible, num, "all")
        }

         if(args[0] == "open"){
             val name = StringBuilder(args[1])
             for (arg in args.slice(2 until args.size)) {
                 name.append(" ").append(arg)
             }
             Desktop.getDesktop().browse(URI(name.toString()))
         }
            }catch (e: Exception){
                e.printStackTrace()
                sender.sendMessage(TextComponentString("Something went wrong. Please try again later. §cError: ${e.message}"))
            }

        }.start()

    }

    fun showItems(sender: ICommandSender, items: List<Item>, page: Int = 0, subcommand: String) {
        var possible = items
        var paged = false
        if(possible.size > PER_PAGE){
            possible = possible.slice(page * PER_PAGE until page * PER_PAGE + PER_PAGE)
            paged = true;
        }
        sender.sendMessage(TextComponentString("§6--- Items §7[${items.size} found] §6---"))
        sender.sendMessage(TextComponentString(""))

        for (item in possible) {
            val component = TextComponentString("§6- ${getColor(item)}${item.name} §8[§7${(if(item.type == null) "None" else item.type)!!.lowercase()}§8]")
            component.style = Style().setHoverEvent(
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    TextComponentString(
                        "${getColor(item)}${item.name} §8[§7${(if(item.type == null) "None" else item.type)!!.uppercase()}§8]\n" +
                                "§7${item.category}\n" +
                                "§7${item.tier}\n§8Click to open in Wynndata"
                    )
                )
            ).setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/items open https://www.wynndata.tk/i/${item.name!!.lowercase().replace(" ", "+")}"))
            sender.sendMessage(component)
        }

        sender.sendMessage(TextComponentString(""))
        if(paged){
            val pages = if(page >= items.size / PER_PAGE) page else page + 1
            val maxPages = items.size / PER_PAGE

            val backComponent = TextComponentString("§7< ")
            backComponent.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString("§7Go back a page")))
                .setClickEvent(ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/items all " + (if(page == 0) 0 else page - 1)
                ))

            backComponent.appendSibling(TextComponentString(" §7${page + 1}§8/§7${maxPages}"))

            println("Max Pages: $maxPages | Next Page: $pages")

            val forwardComponent = TextComponentString(" §7>")
            forwardComponent.style = Style().setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString("§7Go forward a page")))
                .setClickEvent(ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/items $subcommand $pages"
                )
                )
            backComponent.appendSibling(forwardComponent)
            sender.sendMessage(backComponent)
        }
        sender.sendMessage(TextComponentString("§6-------------"))
    }

    fun getColor(item: Item): String {
        return when((if(item.tier == null) "None" else item.tier)!!.lowercase()){
            "normal" -> ChatFormatting.WHITE.toString()
            "unique" -> ChatFormatting.YELLOW.toString()
            "rare" -> ChatFormatting.LIGHT_PURPLE.toString()
            "set" -> ChatFormatting.GREEN.toString()
            "legendary" -> ChatFormatting.AQUA.toString()
            "fabled" -> ChatFormatting.RED.toString()
            "mythic" -> ChatFormatting.DARK_PURPLE.toString()
            else -> ChatFormatting.GRAY.toString()
        }
    }

    fun ICommandSender.send(
        message: String,
        clickText : String? = null,
        clickAction : ClickEvent.Action? = null,
        hoverText : String? = null
    ){
        val component = TextComponentString(message)
        val style = Style()
        if(clickText != null && clickAction != null){
            style.clickEvent = ClickEvent(clickAction, clickText)
        }
        if(hoverText != null){
           style.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentString(hoverText))
        }
        component.style = style
        this.sendMessage(component)
    }


}