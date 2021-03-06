package net.cydhra.technocracy.foundation.content.tileentities.multiblock

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.ecs.tileentities.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.client.gui.multiblock.MultiblockSettingsTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.items.siliconItem
import net.cydhra.technocracy.foundation.content.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.AbstractRectangularMultiBlockTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.AggregatableTileEntityDelegate
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import kotlin.reflect.KClass

/**
 * Base class for parts of a multi block. This class can be instantiated to create parts that do nothing
 *
 * @param clazz the class of the [MultiblockControllerBase] implementation responsible for this specific tile entity
 * @param constructController the constructor for the [MultiblockControllerBase] implementation
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class TileEntityMultiBlockPart<T>(private val clazz: KClass<T>, private val constructController: (World) -> T)
    : TCAggregatableTileEntity by AggregatableTileEntityDelegate(), TCTileEntityGuiProvider, AbstractRectangularMultiBlockTileEntity(), IAggregatableGuiProvider
        where T : MultiblockControllerBase {

    init {
        this.tile = this
    }

    override fun syncDataTo(data: NBTTagCompound?, syncReason: SyncReason?) {
        super.syncDataTo(this.serializeNBT(data!!), syncReason)
    }

    override fun syncDataFrom(data: NBTTagCompound?, syncReason: SyncReason?) {
        super.syncDataFrom(data, syncReason)
        this.deserializeNBT(data!!)
        if (tile.hasWorld())
            tile.world.notifyBlockUpdate(tile.pos, getBlockState(), getBlockState(), 0)
    }

    override fun createNewMultiblock(): T {
        return constructController(this.world)
    }

    override fun getMultiblockControllerType(): Class<T> {
        return clazz.java
    }

    override fun getMultiblockController(): T? {
        @Suppress("UNCHECKED_CAST")
        return super.getMultiblockController() as T?
    }

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun isGoodForSides(validator: IMultiblockValidator?): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.sideBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForFrame(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.frameBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForTop(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.topBlockWhitelist?.test(getBlockState()) ?: true
    }


    override fun isGoodForInterior(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.interiorBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForBottom(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.bottomBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun validateStructure(): Boolean {
        return this.multiblockController?.isAssembled ?: false
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        if (!player.isSneaking) {
            if (!world.isRemote) {
                if (this is ITileEntityMultiblockController && validateStructure()) {
                    player.openGui(TCFoundation, TCGuiHandler.multiblockGui, world, pos.x, pos.y, pos.z)
                }
            }
        }
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {
        val gui = other ?: SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMultiblockTab(this, gui, TCIcon(siliconItem)) {
            override fun init() {
                var nextOutput = 125
                var nextInput = 10
                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth
                var foundProgressComponent: TileEntityProgressComponent? = null
                val sortedComponents = listOf(
                    *(this@TileEntityMultiBlockPart.multiblockController as BaseMultiBlock).getComponents()
                        .toTypedArray()
                )
                    .sortedBy { (_, component) -> component !is TileEntityFluidComponent }
                    .sortedBy { (_, component) -> component !is TileEntityEnergyStorageComponent }
                sortedComponents.forEach { (name, component) ->
                    when (component) {
                        is TileEntityEnergyStorageComponent -> {
                            components.add(DefaultEnergyMeter(nextInput, 20, component, gui))
                            if (inputNearestToTheMiddle < 20) {
                                inputNearestToTheMiddle = 20
                                nextInput = 25
                            }
                        }
                        is TileEntityFluidComponent -> {
                            when {
                                component.fluid.tanktype == DynamicFluidCapability.TankType.INPUT -> {
                                    components.add(DefaultFluidMeter(nextInput, 20, component, gui))
                                    if (inputNearestToTheMiddle < nextInput - 5) {
                                        inputNearestToTheMiddle = nextInput - 5 // 5 is the space between components
                                    }
                                    nextInput += 15 // fluid meter width (10) + space (5)
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.OUTPUT -> {
                                    components.add(DefaultFluidMeter(nextOutput, 20, component, gui))
                                    if (outputNearestToTheMiddle > nextOutput)
                                        outputNearestToTheMiddle = nextOutput
                                    nextOutput += 15
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.BOTH -> {
                                    TODO("not implemented")
                                }
                            }
                        }
                        is TileEntityInventoryComponent -> {
                            when {
                                name.contains("input") -> {
                                    for (i in 0 until component.inventory.slots) {
                                        if (nextInput == 25)
                                            nextInput = 30
                                        components.add(TCSlotIO(component.inventory, i, nextInput, 40, gui))
                                        if (inputNearestToTheMiddle < nextInput)
                                            inputNearestToTheMiddle = nextInput
                                        nextInput += 20
                                    }

                                }
                                name.contains("output") -> {
                                    for (i in component.inventory.slots - 1 downTo 0) {
                                        components.add(TCSlotIO(component.inventory, i, 125 + i * 20, 40, gui))
                                        val newX = 125 + i * 20
                                        if (outputNearestToTheMiddle > newX)
                                            outputNearestToTheMiddle = newX
                                    }
                                }
                            }
                        }
                        is TileEntityProgressComponent -> {
                            foundProgressComponent = component
                        }
                    }
                }
                if (foundProgressComponent != null)
                    components.add(DefaultProgressBar((outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 + inputNearestToTheMiddle, 40, Orientation.RIGHT, foundProgressComponent as TileEntityProgressComponent, gui))

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })
        initGui(gui)
        gui.registerTab(MultiblockSettingsTab(gui, this))
        return gui
    }

    open fun initGui(gui: TCGui) {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return multiblockController?.isAssembled ?: false && this.supportsCapability(capability, facing) && facing != null
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (multiblockController?.isAssembled == true)
            this.castCapability(capability, facing)
        else
            null
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }
}