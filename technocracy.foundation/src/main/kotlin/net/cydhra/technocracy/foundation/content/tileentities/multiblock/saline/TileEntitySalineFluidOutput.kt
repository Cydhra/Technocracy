package net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.multiblock.SalineMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class TileEntitySalineFluidOutput : TileEntityMultiBlockPart<SalineMultiBlock>(SalineMultiBlock::class,
        ::SalineMultiBlock) {

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing == EnumFacing.DOWN) {
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        } else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast<T>(
                    multiblockController?.controllerTileEntity?.fluidOutputComponent?.fluid)
                    ?: DynamicFluidCapability(1) as T
        else
            null
    }
}