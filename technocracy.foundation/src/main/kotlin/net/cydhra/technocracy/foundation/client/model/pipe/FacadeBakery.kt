package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.items.general.FacadeItem
import net.cydhra.technocracy.foundation.util.facade.FakeBlockAccess
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.minecraft.block.Block
import net.minecraft.block.BlockDirectional
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import java.lang.Exception
import java.util.ArrayList


object FacadeBakery {
    fun getFacadeQuads(coverFace: EnumFacing, facadeStack: ItemStack, pos: BlockPos, faces: BooleanArray, currentLayer: BlockRenderLayer): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        val mc = Minecraft.getMinecraft()
        val dispatcher = mc.blockRendererDispatcher

        if (facadeStack.isEmpty || facadeStack.item !is FacadeItem)
            return quads

        val facade = facadeStack.item as FacadeItem
        val stack = facade.getFacadeFromStack(facadeStack)

        val block = Block.getBlockFromItem(stack.stack.item)
        var state: IBlockState = block.getStateFromMeta(stack.stack.itemDamage)

        if (state.properties[BlockDirectional.FACING] != null || state.properties[BlockHorizontal.FACING] != null) {
            state = block.getStateFromMeta(coverFace.ordinal)
        }

        if (state.block.canRenderInLayer(state, currentLayer)) {
            //fix for ctm not working
            val fakeWorld = FakeBlockAccess(mc.world, state, pos)

            //get the custom state with ctm data
            var customState = state.getActualState(fakeWorld, pos)
            val coverModel = dispatcher.getModelForState(customState)
            customState = customState.block.getExtendedState(customState, fakeWorld, pos)

            try {
                quads.addAll(genQuads(coverModel, customState, coverFace, faces, fakeWorld, pos, customState.block.blockLayer == BlockRenderLayer.TRANSLUCENT))
            } catch (e: Exception) {
                TCFoundation.logger.error("Facade of block ${customState.block} has special renderer and references a tile entity that is not in the world")
            }
        }

        return quads
    }

    fun getFacadeItemQuads(facadeItem: ItemStack): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        val coverModel = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(facadeItem, Minecraft.getMinecraft().world, null)
        val faces = BooleanArray(EnumFacing.values().size)

        EnumFacing.values().forEachIndexed { index, enumFacing ->
            faces[index] = enumFacing == EnumFacing.NORTH
        }

        val origQuads = gatherQuads(coverModel)

        for (bakedQuad in origQuads) {
            val quad = SimpleQuad()
            quad.clonePosData = true
            quad.format = DefaultVertexFormats.ITEM
            quad.cloneData(bakedQuad)

            if (bakedQuad.hasTintIndex()) {
                quad.tintColor = Minecraft.getMinecraft().itemColors.colorMultiplier(facadeItem, bakedQuad.tintIndex)
            }

            quad.recalculateVertexPoses(EnumFacing.NORTH, faces)
            quad.recalculateUV()

            quads.add(quad.bake())
        }

        return quads
    }

    private fun gatherQuads(model: IBakedModel): List<BakedQuad> {
        val modelQuads = ArrayList<BakedQuad>()
        for (face in EnumFacing.VALUES) {
            modelQuads.addAll(model.getQuads(null, face, 0))
        }
        modelQuads.addAll(model.getQuads(null, null, 0))
        return modelQuads
    }

    fun genQuads(coverModel: IBakedModel, customState: IBlockState, coverFace: EnumFacing, faces: BooleanArray, access: IBlockAccess, pos: BlockPos, transparent: Boolean): List<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        var origQuads = coverModel.getQuads(customState, null, 0)

        //TODO cleanup
        if (!origQuads.isEmpty()) {
            //Custom model
            origQuads.forEachIndexed { index, bakedQuad ->
                val quad = SimpleQuad()
                quad.clonePosData = true
                quad.format = DefaultVertexFormats.BLOCK
                quad.cloneData(bakedQuad)
                if (bakedQuad.hasTintIndex()) {
                    quad.tintColor = Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex)
                }

                quad.recalculateVertexPoses(coverFace, faces)
                quad.recalculateUV()

                quads.add(quad.bake())
            }
        }

        for (side in EnumFacing.values()) {
            origQuads = coverModel.getQuads(customState, side, 0)
            if (!origQuads.isEmpty()) {
                try {
                    if (origQuads.size != 4) {
                        //Normal block
                        origQuads.forEachIndexed { index, bakedQuad ->
                            val quad = SimpleQuad()
                            quad.clonePosData = true
                            quad.format = DefaultVertexFormats.BLOCK
                            quad.cloneData(bakedQuad)
                            if (bakedQuad.hasTintIndex()) {
                                quad.tintColor = Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex)
                            }

                            quad.recalculateVertexPoses(coverFace, faces)
                            quad.recalculateUV()

                            quads.add(quad.bake())
                        }
                    } else {
                        //ctm block
                        val vertices = mutableListOf<FloatArray>()
                        for (i in 0..3) {
                            vertices.add(generate(FloatArray(3), faces, coverFace, side, i))
                        }
                        val splits = SimpleQuad(vertices).subdivide(4)
                        origQuads.forEachIndexed { index, bakedQuad ->
                            splits[index].format = DefaultVertexFormats.BLOCK
                            splits[index].cloneData(bakedQuad)
                            if (bakedQuad.hasTintIndex()) {
                                splits[index].tintColor = Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex)
                            }

                            splits[index].recalculateUV(index)

                            quads.add(splits[index].bake())
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }


        return quads
    }

    var facadeSize = 1f

    /**
     * Pre generate the quads used for the ctm block
     */
    fun generate(data: FloatArray, faces: BooleanArray, coverFace: EnumFacing?, facing: EnumFacing, vertices: Int): FloatArray {
        val pixelSize = 1 / 16f
        val height = pixelSize * facadeSize

        val minX = 0.0f
        val minY = 0.0f
        val minZ = 0.0f
        val maxX = 1.0f
        val maxY = 1.0f
        val maxZ = 1.0f

        val up = faces[EnumFacing.UP.ordinal]
        val down = faces[EnumFacing.DOWN.ordinal]
        val north = faces[EnumFacing.NORTH.ordinal]
        val south = faces[EnumFacing.SOUTH.ordinal]
        val east = faces[EnumFacing.EAST.ordinal]
        val west = faces[EnumFacing.WEST.ordinal]

        val yWidth = when (coverFace) {
            EnumFacing.UP -> -(1 - height)
            EnumFacing.DOWN -> 1 - height
            else -> 0.0f
        }

        val xWidth = when (coverFace) {
            EnumFacing.EAST -> -(1 - height)
            EnumFacing.WEST -> 1 - height
            else -> 0.0f
        }

        val zWidth = when (coverFace) {
            EnumFacing.SOUTH -> -(1 - height)
            EnumFacing.NORTH -> 1 - height
            else -> 0.0f
        }

        if (facing == EnumFacing.NORTH) {
            when (vertices) {
                1 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                2 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                3 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                0 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.SOUTH) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.WEST) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                3 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.EAST) {
            when (vertices) {
                1 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.UP) {
            when (vertices) {
                0 -> {//2
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                1 -> {//3
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {//0
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {//1
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.DOWN) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        //clamp

        data[0] = MathHelper.clamp(data[0], 0f, 1f)
        data[1] = MathHelper.clamp(data[1], 0f, 1f)
        data[2] = MathHelper.clamp(data[2], 0f, 1f)

        return data
    }
}