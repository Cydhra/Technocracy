package net.cydhra.technocracy.powertools.proxy

import net.cydhra.technocracy.foundation.api.fluids.FluidManager
import net.cydhra.technocracy.foundation.content.blocks.BlockManager
import net.cydhra.technocracy.foundation.content.entities.manager.EntityManager
import net.cydhra.technocracy.foundation.content.items.ItemManager
import net.cydhra.technocracy.foundation.content.tileentities.TileEntityManager
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.powertools.TCPowertools
import net.cydhra.technocracy.powertools.client.powertoolsCreativeTab
import net.cydhra.technocracy.powertools.content.item.*
import net.cydhra.technocracy.powertools.content.listener.ItemLogicEventHandler
import net.cydhra.technocracy.powertools.network.ClientInputPacket
import net.cydhra.technocracy.powertools.util.PlayerInputs
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side

open class CommonProxy {

    protected lateinit var blockManager: BlockManager
    protected lateinit var fluidManager: FluidManager
    protected lateinit var itemManager: ItemManager
    protected lateinit var tileEntityManager: TileEntityManager
    protected lateinit var entityManager: EntityManager

    /**
     * Initialize the class properties. This should not be done earlier, as contents of the properties might access
     * the config, which isn't loaded at instantiation of the proxy.
     */
    fun initializeProxy() {
        blockManager = BlockManager(TCPowertools.MODID, powertoolsCreativeTab)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCPowertools.MODID, powertoolsCreativeTab)
        tileEntityManager = TileEntityManager(TCPowertools.MODID)
        entityManager = EntityManager(TCPowertools.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(entityManager)

        itemManager.prepareItemForRegistration(batteryUpgrade_One)
        itemManager.prepareItemForRegistration(batteryCapacityUpgrade)

        itemManager.prepareItemForRegistration(modularItem)
        itemManager.prepareItemForRegistration(energyShield)

        itemManager.prepareItemForRegistration(modularhelmet)
        itemManager.prepareItemForRegistration(modularboots)
        itemManager.prepareItemForRegistration(modularchestplate)
        itemManager.prepareItemForRegistration(modularleggings)

        itemManager.prepareItemForRegistration(armorUpgrade_IronPlating)
        itemManager.prepareItemForRegistration(armorUpgrade_DiamondPlating)
        itemManager.prepareItemForRegistration(chestplateUpgradeFireResistance)
        itemManager.prepareItemForRegistration(helmetUpgradeAquaAffinityItem)
        itemManager.prepareItemForRegistration(bootsUpgradeFeatherFallItem)
        itemManager.prepareItemForRegistration(bootsUpgradePropulsionItem)
        itemManager.prepareItemForRegistration(toolUpgradeAxe)
        itemManager.prepareItemForRegistration(toolUpgradePickaxe)
        itemManager.prepareItemForRegistration(toolUpgradeShovel)

        itemManager.prepareItemForRegistration(helmetUpgradeWaterBreathing)
        itemManager.prepareItemForRegistration(helmetUpgradeNightVision)
        itemManager.prepareItemForRegistration(chestplateUpgradeJetpackItem)

        PacketHandler.registerPacket(ClientInputPacket::class.java, ClientInputPacket::class.java, Side.SERVER)

        MinecraftForge.EVENT_BUS.register(ItemLogicEventHandler)
        MinecraftForge.EVENT_BUS.register(PlayerInputs)
    }

    open fun init() {

    }

    open fun postInit() {

    }
}