package net.cydhra.technocracy.powertools.content.item.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.powertools.content.item.logic.FireExtinguishLogic
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import java.util.*


/**
 * Upgrades energy capacity of something
 */
class CapacityUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_CAPACITY)

/**
 * Upgrades energy consumption of items. All logic clients that consume energy, should respect an energy consumption
 * multiplier
 */
class EnergyEfficiencyUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_USAGE)

/**
 * Upgrades digging speed of tools
 */
class DigSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_DIG_SPEED)

/**
 * Upgrades armor of armor using a multiplier. (duh)
 */
class ArmorUpgrade(additive: Double) : ItemMultiplierUpgrade(additive, UPGRADE_ARMOR_ARMOR)
class ArmorToughnessUpgrade(additive: Double) : ItemMultiplierUpgrade(additive, UPGRADE_ARMOR_TOUGHNESS)

/**
 * Upgrades a multiplier on walking speed of armor
 */
class WalkSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_WALK_SPEED)

/**
 * Upgrades an attack speed multiplier of an item
 */
class AttackSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_SPEED)

/**
 * Upgrades an attack damage multiplier of an item
 */
class AttackDamageUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_DAMAGE)

class FireExtinguishUpgrade : ItemUpgrade() {
    override val upgradeParameter = UPGRADE_GENERIC

    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return !upgradable.hasLogicStrategy("FireExtinguish")
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.removeLogicStrategy("FireExtinguish")
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        if (!upgradable.hasLogicStrategy("FireExtinguish"))
            upgradable.addLogicStrategy(FireExtinguishLogic(), "FireExtinguish")
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(
                TextComponentTranslation("tooltips.upgrades.hint.antifire")
                        .setStyle(Style().setColor(TextFormatting.GREEN)))
    }
}