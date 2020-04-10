package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.api.upgrades.Upgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.minecraft.util.text.ITextComponent
import java.util.*

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class MultiplierUpgrade(
        val multiplier: Double,
        override val upgradeParameter: UpgradeParameter
) : Upgrade<TCMachineTileEntity> {

    override fun canInstallUpgrade(upgradable: TCMachineTileEntity,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUninstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUpgradeLoad(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}