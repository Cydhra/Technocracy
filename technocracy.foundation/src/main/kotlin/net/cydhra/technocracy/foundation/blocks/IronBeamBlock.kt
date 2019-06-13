package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractRotateableBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class IronBeamBlock : AbstractRotateableBlock("iron_beam", material = Material.IRON) {

    init {
        this.soundType = SoundType.METAL
        this.blockHardness = 3f
        this.blockResistance = 5f
    }

    override fun isToolEffective(type: String, state: IBlockState): Boolean {
        return type == "pickaxe"
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    @Suppress("OverridingDeprecatedMember") // implementing is fine
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }
}