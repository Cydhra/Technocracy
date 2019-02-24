package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.creativetab.CreativeTabs

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class IngotItem(ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("ingot.$ingotMaterialName", itemColor = color) {

    override val modelLocation: String
        get() = "${TCFoundation.MODID}:ingot"

    init {
        this.creativeTab = CreativeTabs.MATERIALS
    }
}