package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.IComponent

interface TCAggregatableTileEntity : TCTileEntity {

    fun getComponents(): MutableList<Pair<String, IComponent>>

    fun registerComponent(component: IComponent, name: String)
}