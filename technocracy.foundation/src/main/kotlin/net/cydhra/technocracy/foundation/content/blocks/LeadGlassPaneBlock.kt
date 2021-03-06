package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.api.blocks.IBaseBlock
import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor
import net.minecraft.block.BlockPane
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material

class LeadGlassPaneBlock : BlockPane(Material.GLASS, true), IBaseBlock {
    override val modelLocation: String
        get() = this.registryName.toString()

    override val generateItem: Boolean
        get() = true

    override val colorMultiplier: IBlockColor? = null

    init {
        this.unlocalizedName = "lead_glass_pane"
        this.setRegistryName("lead_glass_pane")
        this.soundType = SoundType.GLASS
    }
}