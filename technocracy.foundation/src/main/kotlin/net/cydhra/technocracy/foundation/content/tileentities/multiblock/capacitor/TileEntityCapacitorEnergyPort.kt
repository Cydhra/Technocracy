package net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor

import net.cydhra.technocracy.foundation.content.multiblock.CapacitorMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage

class TileEntityCapacitorEnergyPort : TileEntityMultiBlockPart<CapacitorMultiBlock>(CapacitorMultiBlock::class,
        ::CapacitorMultiBlock) {

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null) {
            capability == CapabilityEnergy.ENERGY
        } else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityEnergy.ENERGY.cast<T>(
                    multiblockController?.controllerTileEntity?.energyStorageComponent?.energyStorage)
                    ?: EnergyStorage(1) as T
        else
            null
    }
}

