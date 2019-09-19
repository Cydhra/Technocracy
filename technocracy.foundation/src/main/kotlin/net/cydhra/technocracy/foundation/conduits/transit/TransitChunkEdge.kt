package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * A transit network edge that connects two chunks.
 */
class TransitChunkEdge() : TransitEdge() {

    constructor(id: Int, type: PipeType, facing: EnumFacing, pos: BlockPos) : this() {
        this.id = id
        this.type = type
        this.facing = facing
    }
}