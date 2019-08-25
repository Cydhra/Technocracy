package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.kilnBlock
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class PyrolysisKilnRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<PyrolysisKilnRecipeCategory.PyrolysisKilnRecipeWrapper>(
        guiHelper,
        kilnBlock,
        RecipeManager.RecipeType.KILN,
        PyrolysisKilnRecipeWrapper::class.java,
        TCCategoryUid.PYROLYSIS
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(23, 38, Orientation.RIGHT, null, null).getDrawable(100)
    private val fluidInputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 10, 75, 10, 50).build() //TODO move to superclass
    private val fluidOutputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 0, 75, 10, 50).build() //TODO move to superclass

    override fun getTitle(): String = "Pyrolysis Kiln"

    override fun setRecipe(layout: IRecipeLayout, wrapper: PyrolysisKilnRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks
        val itemStacks = layout.itemStacks

        fluidStacks.init(0, true, 10, 10, 10, 50, 1000, false, fluidInputOverlay)
        fluidStacks.init(1, false, 48, 10, 10, 50, 1000, false, fluidOutputOverlay)

        fluidStacks.set(ingredients)
        itemStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class PyrolysisKilnRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}