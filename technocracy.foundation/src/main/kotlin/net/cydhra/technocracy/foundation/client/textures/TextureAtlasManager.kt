package net.cydhra.technocracy.foundation.client.textures

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.util.DynamicTextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class TextureAtlasManager {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    companion object {
        lateinit var connector_energy: TextureAtlasSprite
        lateinit var connector_inventory: TextureAtlasSprite
        lateinit var pipe_node: TextureAtlasSprite

        lateinit var drum_iron: TextureAtlasSprite
        lateinit var drum_iron_top: TextureAtlasSprite
        lateinit var drum_steel: TextureAtlasSprite
        lateinit var drum_steel_top: TextureAtlasSprite

        val pipeTextures = mutableMapOf<PipeType, DynamicTextureAtlasSprite>()

        private lateinit var textureMap: TextureMap

        /**
         * Returns a texture atlas sprite for a resource location of the texture map
         */
        fun getTextureAtlasSprite(location: ResourceLocation): TextureAtlasSprite {
            return textureMap.getTextureExtry(location.toString()) ?: textureMap.missingSprite
        }

        /**
         * Returns a resource location mapped to the technocracy resource domain
         */
        fun getResourceLocation(name: String): ResourceLocation {
            return ResourceLocation("technocracy.foundation", name)
        }
    }

    fun getDynamicAnimatedSprite(location: ResourceLocation): DynamicTextureAtlasSprite {
        val sprite = textureMap.getTextureExtry(location.toString())
        if (sprite == null) {
            val dynamicSprite = DynamicTextureAtlasSprite.makeAtlasSprite(location)
            textureMap.setTextureEntry(dynamicSprite)
            return dynamicSprite
        }

        return sprite as DynamicTextureAtlasSprite
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAtlas(event: TextureStitchEvent.Pre) {
        textureMap = event.map
        connector_energy = event.map.registerSprite(getResourceLocation("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getResourceLocation("extra/connector_inventory"))

        pipeTextures[PipeType.ITEM] = getDynamicAnimatedSprite(getResourceLocation("block/pipe_item"))
        pipeTextures[PipeType.FLUID] = getDynamicAnimatedSprite(getResourceLocation("block/pipe_fluid"))
        pipeTextures[PipeType.ENERGY] = getDynamicAnimatedSprite(getResourceLocation("block/pipe_energy"))

        pipe_node = event.map.registerSprite(getResourceLocation("block/frame_corners"))

        drum_iron = event.map.registerSprite(getResourceLocation("block/drum/drum_iron"))
        drum_iron_top = event.map.registerSprite(getResourceLocation("block/drum/drum_iron_top"))
        drum_steel = event.map.registerSprite(getResourceLocation("block/drum/drum_steel"))
        drum_steel_top = event.map.registerSprite(getResourceLocation("block/drum/drum_steel_top"))

        event.map.registerSprite(ResourceLocation("technocracy.foundation", "liquid/fluid_opaque_still"))
        event.map.registerSprite(ResourceLocation("technocracy.foundation", "liquid/fluid_still"))
        event.map.registerSprite(ResourceLocation("technocracy.foundation", "liquid/fluid_opaque_flow"))
        event.map.registerSprite(ResourceLocation("technocracy.foundation", "liquid/fluid_flow"))
    }
}