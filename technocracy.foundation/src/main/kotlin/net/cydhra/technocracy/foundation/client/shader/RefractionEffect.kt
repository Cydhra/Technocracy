package net.cydhra.technocracy.foundation.client.shader

import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

object RefractionEffect {
    var built = false

    private lateinit var refracShader: BasicShaderProgram

    lateinit var colorShift: BasicShaderProgram.ShaderUniform
    lateinit var colorShiftAmount: BasicShaderProgram.ShaderUniform
    lateinit var time: BasicShaderProgram.ShaderUniform

    //var time = Vector2f(0f, 0f)

    private lateinit var buffer: Framebuffer

    private var rebuild = true

    /**
     * Updates the backbuffer texture the the vanilla texture
     */
    fun updateBackBuffer() {
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().framebuffer.framebufferObject)
        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.framebufferObject)

        GL30.glBlitFramebuffer(0, 0, buffer.framebufferWidth, buffer.framebufferHeight, 0, 0, buffer.framebufferWidth, buffer.framebufferHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST)

        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().framebuffer.framebufferObject)
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer.framebufferObject)
    }

    /**
     * Generates the shader and binds the textures
     */
    fun preRenderType(diffuseTexture: ResourceLocation, normalTexture: ResourceLocation) {
        if (!built) {
            built = true

            buffer = Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false)

            refracShader = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/refrag.vsh"), ResourceLocation("technocracy.foundation", "shaders/refrag.fsh"))
            colorShift = refracShader.getUniform("colorShift", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
            colorShiftAmount = refracShader.getUniform("colorShiftAmount", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
            time = refracShader.getUniform("time", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)
            refracShader.getUniform("image", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
            refracShader.getUniform("image_normal", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(2)
            refracShader.getUniform("image_diffuse", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(3)
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            if (rebuild) {
                rebuild = false
                refracShader.reloadShader()
            }
        } else {
            rebuild = true
        }


        refracShader.start()
        //timeUniform.uploadUniform(time.x + mc.renderPartialTicks, time.y + mc.renderPartialTicks)

        buffer = buffer.validateAndClear()
        Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

        GlStateManager.bindTexture(buffer.framebufferTexture)
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
        Minecraft.getMinecraft().renderEngine.bindTexture(normalTexture)
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2 + 1)
        Minecraft.getMinecraft().renderEngine.bindTexture(diffuseTexture)
    }

    /**
     * Uploads the uniforms
     */
    fun beginRendering() {
        refracShader.updateUniforms()
    }

    /**
     * Unbinds the shader and resets the bound textures
     */
    fun postRenderType() {
        refracShader.stop()

        GlStateManager.bindTexture(0)
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
        GlStateManager.bindTexture(0)
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        GlStateManager.bindTexture(0)

        Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
    }
}