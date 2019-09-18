package net.cydhra.technocracy.foundation.tileentity.multiblock.tank

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.multiblock.TankMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.OptionalAttachedComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing


open class TileEntityTankMultiBlockPart : TileEntityMultiBlockPart<TankMultiBlock>(TankMultiBlock::class,
        ::TankMultiBlock), ITileEntityMultiblockController {


    val fluidComp = OptionalAttachedComponent(FluidComponent(DynamicFluidHandler(), EnumFacing.values().toMutableSet()))

    init {
        this.registerComponent(fluidComp, "fluidComponent")
    }
}