package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.heatmeter.DefaultHeatMeter
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11


class CoolantMeter(posX: Int, posY: Int, val coolantIn: FluidTileEntityComponent, val coolantOut: FluidTileEntityComponent, val heat: HeatStorageTileEntityComponent, val gui: TCGui) : FluidMeter(posX, posY, coolantIn) {

    val meterIn = DefaultFluidMeter(6, 7, coolantIn, gui)
    val meterOut = DefaultFluidMeter(6 + 16 + 8 + 4, 7, coolantOut, gui)
    val heatBar = DefaultHeatMeter(6 + 16 + 2, 7, heat, gui)

    override var width = 64
    override var height = 64


    companion object {
        val coolantTexture: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/coolant_addon.png")
    }

    override fun drawOverlay(x: Int, y: Int) {
    }

    override fun drawBackground(x: Int, y: Int) {
        GlStateManager.enableBlend()
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(coolantTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x + posX, y + posY, 0f, 0f, width, height, 64f, 64f)
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        if (partialTicks != -1f) {
            drawBackground(x, y)
        }

        val partialTicks = Minecraft.getMinecraft().renderPartialTicks

        meterIn.draw(x + posX, y + posY, mouseX, mouseY, partialTicks)
        meterOut.draw(x + posX, y + posY, mouseX, mouseY, partialTicks)
        heatBar.draw(x + posX, y + posY, mouseX, mouseY, partialTicks)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        when {
            meterIn.isMouseOnComponent(mouseX - posX, mouseY - posY) -> {
                meterIn.drawTooltip(mouseX, mouseY)
            }
            meterOut.isMouseOnComponent(mouseX - posX, mouseY - posY) -> {
                meterOut.drawTooltip(mouseX, mouseY)
            }
            heatBar.isMouseOnComponent(mouseX - posX, mouseY - posY) -> {
                heatBar.drawTooltip(mouseX, mouseY)
            }
        }
    }
}