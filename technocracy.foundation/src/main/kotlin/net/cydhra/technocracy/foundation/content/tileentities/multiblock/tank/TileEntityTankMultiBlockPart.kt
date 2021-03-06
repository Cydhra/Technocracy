package net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank

import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.multiblock.TankMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityOptionalAttachedComponent
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


open class TileEntityTankMultiBlockPart : TileEntityMultiBlockPart<TankMultiBlock>(TankMultiBlock::class,
        ::TankMultiBlock), ITileEntityMultiblockController {


    val fluidComp = TileEntityOptionalAttachedComponent(TileEntityFluidComponent(DynamicFluidCapability(), EnumFacing.values().toMutableSet()))


    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = SimpleGui(guiWidth = 176, guiHeight = 200, container = TCContainer(this))

        //176, val guiHeight: Int = 166

        gui.registerTab(object : TCTab("Tank", gui) {
            override fun init() {

                val fluid = DefaultFluidMeter(8, 20, this@TileEntityTankMultiBlockPart.multiblockController!!.controllerTileEntity!!.fluidComp.innerComponent, gui)
                fluid.height = gui.guiHeight - 58 - 16 - 5 - 12 - 25
                fluid.width = 8 * 18 - 9

                components.add(fluid)

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })
        initGui(gui)
        return gui
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return false
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true

        val controller = this.multiblockController ?: return true

        //allow for bigger interaction range
        val size = controller.maximumCoord.distanceSq(controller.minimumCoord)

        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16 + size
    }

    init {
        fluidComp.innerComponent.fluid.fluidChangeThreshold = 1f

        fluidComp.innerComponent.syncToClient = true
        this.registerComponent(fluidComp, "fluidComponent")
    }
}