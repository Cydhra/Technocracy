package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.astronautics.client.astronauticsColorTabs
import net.cydhra.technocracy.foundation.content.blocks.ColoredPlainBlock
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class ReinforcedConcreteBlock : ColoredPlainBlock("reinforced_concrete", Material.IRON, colorTab = astronauticsColorTabs) {
    init {
        setHardness(10F)
        setResistance(2000.0f)
    }

    override fun onEntityWalk(worldIn: World, pos: BlockPos, entityIn: Entity) {
        entityIn.motionX *= 1.2
        entityIn.motionZ *= 1.2
    }
}