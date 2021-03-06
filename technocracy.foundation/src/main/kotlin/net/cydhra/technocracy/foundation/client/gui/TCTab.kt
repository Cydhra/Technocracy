package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotPlayer
import net.cydhra.technocracy.foundation.util.applyAll
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse

/**
 * A tab within a technocracy-style GUI. Each technocracy-style gui is composed of one or more tabs, that can be
 * accessed via the right side of the gui, where all tabs are displayed with their respective icon and can be selected.
 *
 * @param name tab name (displayed at the top of the tab)
 * @param parent the parent [TCGui] that owns the tab
 * @param tint tint color of the tab at the right side. Defaults to white
 * @param icon tab icon resource location. Defaults to a non-tinted gear. Should be an item texture.
 */
abstract class TCTab(val name: String, val parent: TCGui, val tint: Int = -1,
                     val icon: TCIcon = TCIcon(ResourceLocation("technocracy.foundation", "textures/item/gear.png"))) {

    val components: ArrayList<ITCComponent> = ArrayList()

    abstract fun init()

    /**
     * called when the gui is beeing resized to reset values
     */
    open fun onResize() {}

    fun <T : ITCComponent> ArrayList<ITCComponent>.addElement(element: T): T {
        components.add(element)
        return element
    }

    open fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(name, 8f + x, 8f + y, 4210752, false)

        this.components.forEach {
            it.draw(x, y, mouseX, mouseY, partialTicks)
        }
    }

    open fun drawToolTips(mouseX: Int, mouseY: Int) {
        components.forEach {
            if (it.isMouseOnComponent(mouseX, mouseY))
                it.drawTooltip(mouseX, mouseY)
        }
    }

    open fun update() {
        this.components.forEach(ITCComponent::update)
    }

    open fun onClose() {}

    open fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        components.forEach {
            if (it.isMouseOnComponent(mouseX - x, mouseY - y))
                it.mouseClicked(x, y, mouseX, mouseY, mouseButton)
        }
    }

    open fun handleMouseInput() {
        val i = Mouse.getEventDWheel()
        components.forEach { it.onMouseScroll(i) }
    }

    protected fun addPlayerInventorySlots(player: EntityPlayer, x: Int, y: Int) {
        this.components.add(DefaultLabel(x, y, player.inventory.displayName.unformattedText, 4210752, false, parent))

        val yOffset = y + 12

        for (row in 0..2) {
            for (slot in 0..8) {
                this.components.add(TCSlotPlayer(player.inventory, slot + row * 9 + 9,
                        x + slot * 18, yOffset + row * 18, parent))
            }
        }

        for (k in 0..8) {
            this.components.add(TCSlotPlayer(player.inventory, k, x + k * 18, yOffset + 58, parent))
        }
    }

    protected fun addComponent(component: TCComponent) {
        components.add(component)
    }

    open fun getSizeX(): Int {
        return parent.origWidth
    }

    open fun getSizeY(): Int {
        return parent.origHeight
    }

}
