package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCSlot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

abstract class TCTab(val name: String, val parent: TCGui, val tint: Int = -1,
                     val icon: ResourceLocation? =
                             ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {


    val components: ArrayList<TCComponent> = ArrayList()

    abstract fun init()

    open fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.components.forEach {
            it.draw(mouseX, mouseY, partialTicks)
        }
    }

    open fun update() {
        this.components.forEach(TCComponent::update)
    }

    protected fun addPlayerInventorySlots(player: EntityPlayer, x: Int, y: Int) {
        for (row in 0..2) {
            for (slot in 0..8) {
                this.components.add(TCSlot(player.inventory, slot + row * 9 + 9,
                        x + slot * 18, y + row * 18))
            }
        }

        for (k in 0..8) {
            this.components.add(TCSlot(player.inventory, k, x + k * 18, y + 58))
        }
    }

}