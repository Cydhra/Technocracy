package net.cydhra.technocracy.foundation.client.model

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraft.client.resources.IResourceManagerReloadListener

class CustomModelLoader(val builtInModels: Map<String, IModel>) : ICustomModelLoader {
    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return if (modelLocation.resourceDomain != TCFoundation.MODID) {
            false
        } else this.builtInModels.containsKey(modelLocation.resourcePath)
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel? {
        return this.builtInModels.get( modelLocation.getResourcePath() );
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        for (model in this.builtInModels.values) {
            if (model is IResourceManagerReloadListener) {
                (model as IResourceManagerReloadListener).onResourceManagerReload(resourceManager)
            }
        }
    }


}