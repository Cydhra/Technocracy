package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.blocks.color.ConstantBlockColor
import net.minecraft.block.material.Material
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Class for all ore type blocks of this modification
 */
class OreBlock(unlocalizedName: String, oreType: String, colorMultiplier: Int)
    : AbstractBaseBlock("$oreType.$unlocalizedName",
        colorMultiplier = ConstantBlockColor(colorMultiplier),
        material = Material.ROCK,
        oreDictionaryName = "ore${unlocalizedName.capitalize()}") {

    override val modelLocation: String = "${TCFoundation.MODID}:$oreType"

    init {
        this.blockResistance = 3.5f
        this.blockHardness = 3f
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }
}