package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityDissolutionChamber : MachineTileEntity(), TEInventoryProvider<DynamicInventoryCapability> {

    private val inputInventoryComponent =
        TileEntityInventoryComponent(1, this, EnumFacing.WEST, DynamicInventoryCapability.InventoryType.INPUT)
    private val inputFluidComponent = TileEntityFluidComponent(
        4000,
        tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP)
    )
    private val outputFluidComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.DISSOLUTION) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputFluidComponent, "output_fluid")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.DISSOLUTION,
                inputInventory = this.inputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                outputFluidSlots = arrayOf(outputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 100,
                progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}