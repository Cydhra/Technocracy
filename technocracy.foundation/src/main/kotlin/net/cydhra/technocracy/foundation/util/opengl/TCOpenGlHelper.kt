package net.cydhra.technocracy.foundation.util.opengl

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.ARBDrawBuffers
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GLContext
import java.nio.IntBuffer


object TCOpenGlHelper {

    private val drawBuffers: GLType

    init {
        val contextcapabilities = GLContext.getCapabilities()

        val gl20 = contextcapabilities.OpenGL20
        val arb_draw_buffers = contextcapabilities.GL_ARB_draw_buffers

        drawBuffers = if (gl20) GLType.BASE else if (arb_draw_buffers) GLType.ARB else GLType.NONE
    }

    fun glDrawBuffers(buffer: IntBuffer) {
        if (drawBuffers == GLType.BASE) {
            GL20.glDrawBuffers(buffer)
        } else if (drawBuffers == GLType.ARB) {
            ARBDrawBuffers.glDrawBuffersARB(buffer)
        }
        buffer.rewind()
    }

    fun glDrawBuffers(buffer: Int) {
        if (drawBuffers == GLType.BASE) {
            GL20.glDrawBuffers(buffer)
        } else if (drawBuffers == GLType.ARB) {
            ARBDrawBuffers.glDrawBuffersARB(buffer)
        }
    }

    @SideOnly(Side.CLIENT)
    internal enum class GLType {
        BASE,
        ARB,
        EXT,
        NONE
    }
}