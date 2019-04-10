package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorageStategy
import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.tileentity.components.IComponent
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgrades
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractMachine(private val energyStorage: DynamicEnergyStorage) : TileEntity(), ITickable {

    protected var rotation = EnumFacing.NORTH
    protected var redstoneMode = RedstoneMode.IGNORE

    protected val upgrades = MachineUpgrades()

    protected val components = HashSet<IComponent>()

    init {
        components.add(upgrades)
        TileEntity.register("${TCFoundation.MODID}:${javaClass.simpleName}", this.javaClass)
    }

    override fun readFromNBT(nbtTags: NBTTagCompound) {
        super.readFromNBT(nbtTags)

        if (nbtTags.hasKey("facing")) {
            rotation = EnumFacing.values()[(nbtTags.getInteger("facing"))]
        }
        if (nbtTags.hasKey("redstone")) {
            redstoneMode = RedstoneMode.values()[(nbtTags.getInteger("redstone"))]
        }

        if(nbtTags.hasKey("energy")) {
            DynamicEnergyStorageStategy.readNBT(this.energyStorage, nbtTags.getCompoundTag("energy"))
        }

        for (comp in components) {
            comp.readFromNBT(nbtTags)
        }

    }

    override fun writeToNBT(nbtTags: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbtTags)

        nbtTags.setInteger("facing", rotation.ordinal)
        nbtTags.setInteger("redstone", redstoneMode.ordinal)
        nbtTags.setTag("energy", DynamicEnergyStorageStategy.writeNBT(this.energyStorage))

        for (comp in components) {
            comp.writeToNBT(nbtTags)
        }

        return nbtTags
    }

    abstract override fun update()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (capability == EnergyCapabilityProvider.CAPABILITY_ENERGY)
            true
        else
            super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == EnergyCapabilityProvider.CAPABILITY_ENERGY)
            EnergyCapabilityProvider.CAPABILITY_ENERGY!!.cast<T>(this.energyStorage)
        else
            super.getCapability(capability, facing)
    }

    enum class RedstoneMode {
        HIGH, LOW, IGNORE
    }
}