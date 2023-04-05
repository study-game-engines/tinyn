package com.github.minigdx.tiny.resources

import com.github.minigdx.tiny.engine.GameOption
import com.github.minigdx.tiny.graphic.FrameBuffer
import com.github.minigdx.tiny.input.InputHandler
import com.github.minigdx.tiny.lua.CtrlLib
import com.github.minigdx.tiny.lua.GfxLib
import com.github.minigdx.tiny.lua.MapLib
import com.github.minigdx.tiny.lua.TinyLib
import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.Companion.valueOf
import org.luaj.vm2.Varargs
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.BaseLib
import org.luaj.vm2.lib.Bit32Lib
import org.luaj.vm2.lib.CoroutineLib
import org.luaj.vm2.lib.PackageLib
import org.luaj.vm2.lib.StringLib
import org.luaj.vm2.lib.TableLib

class GameScript(
    val name: String,
    val gameOption: GameOption,
    val inputHandler: InputHandler,
    override val type: ResourceType
): GameResource {

    var exited: Boolean = false
    var evaluated: Boolean = false
    var loading: Boolean = false
    var reloaded: Boolean = false

    override var reload: Boolean = false
    override var isLoaded: Boolean = false

    var content: ByteArray = ByteArray(0)

    var spriteSheets: Map<ResourceType, SpriteSheet> = emptyMap()

    var level: GameLevel? = null

    private var initFunction: LuaValue? = null
    private var updateFunction: LuaValue? = null
    private var drawFunction: LuaValue? = null
    private var setStateFunction: LuaValue? = null
    private var getStateFunction: LuaValue? = null

    private var globals: Globals? = null

    lateinit var frameBuffer: FrameBuffer

    class State(val args: LuaValue)

    private fun createLuaGlobals(): Globals = Globals().apply {
        load(BaseLib())
        load(PackageLib())
        load(Bit32Lib())
        load(TableLib())
        load(StringLib())
        load(CoroutineLib())
        load(TinyLib(this@GameScript))
        load(MapLib(this@GameScript))
        load(GfxLib(this@GameScript))
        load(CtrlLib(inputHandler))
        LoadState.install(this)
        LuaC.install(this)
    }

    fun isValid(content: ByteArray): Boolean {
        return try {
            createLuaGlobals().load(content.decodeToString()).call()
            true
        } catch (exception: LuaError) {
            println("Can't parse '$name' as the file is not valid.")
            exception.printStackTrace()
            false
        }
    }


    fun evaluate() {
        globals = createLuaGlobals()

        evaluated = true
        loading = false
        reloaded = false
        exited = false

        globals?.load(content.decodeToString())?.call()

        initFunction = globals?.get("_init")?.nullIfNil()
        updateFunction = globals?.get("_update")?.nullIfNil()
        drawFunction = globals?.get("_draw")?.nullIfNil()
        getStateFunction = globals?.get("_getState")?.nullIfNil()
        setStateFunction = globals?.get("_setState")?.nullIfNil()

        initFunction?.call(valueOf(gameOption.width), valueOf(gameOption.height))
    }

    internal fun invoke(name: String, vararg args: LuaValue) {
        @Suppress("UNCHECKED_CAST")
        globals?.get(name)?.nullIfNil()?.invoke(args as Array<LuaValue>)
    }

    fun getState(): State? {
        val data = getStateFunction?.call()
        return if (data != null && !data.isnil()) {
            return State(data)
        } else {
            null
        }
    }

    fun setState(state: State? = null) {
        if (state != null) {
            setStateFunction?.call(state.args)
        }

    }

    fun advance() {
        updateFunction?.call()
        drawFunction?.call()
    }

    fun resourcesLoaded() {
        globals?.get("_resources")?.nullIfNil()?.call()
    }

    private fun LuaValue.nullIfNil(): LuaValue? {
        return if (this.isnil()) {
            null
        } else {
            this
        }
    }

    override fun toString(): String {
        return """--- LUA SCRIPT ($name) ---
existed: $exited | evaluated: $evaluated | loading: $loading | reloaded: $reloaded |
init: ${initFunction != null} | update: ${updateFunction != null} | draw: ${drawFunction != null} |
            ----
            ${content.decodeToString()}
"""
    }
}
