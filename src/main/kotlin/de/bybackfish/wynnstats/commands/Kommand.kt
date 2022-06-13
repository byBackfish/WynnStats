package de.bybackfish.wynnstats.commands

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.World
import net.minecraftforge.client.ClientCommandHandler

open class Kommand(
    private val k: Kommand.(args: Array<out String>) -> Unit = {},
    private val commandName: String,
    private val alias: Array<String> = arrayOf(),
    private val usages: Array<String> = arrayOf(),
    private val autoComplete: ((args: Array<out String>) -> MutableList<String>)? = null,
    private val subcommand: Boolean = false
) : CommandBase() {

    lateinit var mc: Minecraft
    lateinit var player: EntityPlayerSP
    lateinit var world: World
    var inited = false

    init {
        ClientCommandHandler.instance.registerCommand(this)
    }

    override fun getName(): String = this.commandName
    override fun getUsage(sender: ICommandSender) = "/${commandName}"
    override fun getAliases(): MutableList<String> = alias.toMutableList()
    override fun getRequiredPermissionLevel() = 0
    override fun getTabCompletions(
        server: MinecraftServer?,
        sender: ICommandSender,
        args: Array<out String>,
        targetPos: BlockPos?
    ): MutableList<String> {
        if(autoComplete == null) return super.getTabCompletions(server, sender, args, targetPos);
        return super.getTabCompletions(server, sender, args, targetPos)
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        setup()
        Thread {
            try {
                k(args ?: arrayOf())
            } catch (e: Exception) {
                send(e.message ?: "")
            }
        }.start()
    }

    private fun setup() {
        if (inited) return
        inited = true
        player = Minecraft.getMinecraft().player
        mc = Minecraft.getMinecraft()
        world = mc.world

    }

    fun send(message: String, args: Array<out String> = arrayOf()) {
        var msg = message
        for ((index, arg) in args.withIndex()) {
            msg = msg.replace("%s", arg).replace("%${index + 1}", arg)
        }
        send(TextComponentString(msg))
    }

    fun send(message: ITextComponent) {
        player.sendMessage(message)
    }

    fun sendUsage() {
        send("§c✪ §7${commandName} Command Usage")
        for (i in usages) {
            val usage = i.split(";")[0]
            val description = i.split(";")[1]
            send("${"".repeat(3)}§8${usage}: §7${description}")
        }
        send("\n")
    }

    fun createComponent(
        content: String,
        hoverEvent: HoverEvent? = null,
        clickEvent: ClickEvent? = null
    ): TextComponentString {
        val component = TextComponentString(content).apply {
            style = Style()
            if (hoverEvent != null)
                style.hoverEvent = hoverEvent
            if (clickEvent != null)
                style.clickEvent = clickEvent

        }
        return component
    }
}

