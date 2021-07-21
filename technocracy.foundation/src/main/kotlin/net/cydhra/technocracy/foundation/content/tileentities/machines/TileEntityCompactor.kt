package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityCompactor : MachineTileEntity(), TEInventoryProvider<DynamicInventoryCapability> {

    /**
     * Input inventory for the furnace with one slot
     */
    private val inputInventoryComponent =
        TileEntityInventoryComponent(1, this, EnumFacing.WEST, DynamicInventoryCapability.InventoryType.INPUT)

    /**
     * Output inventory for the furnace with one slot
     */
    private val outputInventoryComponent = TileEntityInventoryComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.COMPACTOR) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.COMPACTOR,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 10,
                progress = this.progressComponent), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return (inventory == inputInventoryComponent.inventory && this.recipes.any { it.getInput()[0].test(stack) }) ||
                (inventory == outputInventoryComponent.inventory && this.recipes
                        .any { it.getOutput()[0].item == stack.item })
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}