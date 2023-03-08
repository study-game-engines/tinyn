import com.github.minigdx.tiny.engine.GameEngine
import com.github.minigdx.tiny.engine.GameOption
import com.github.minigdx.tiny.file.CommonVirtualFileSystem
import com.github.minigdx.tiny.log.StdOutLogger
import com.github.minigdx.tiny.platform.glfw.GlfwPlatform

fun main(args: Array<String>) {

    // https://docs.oracle.com/javase/tutorial/2d/images/saveimage.html
    // https://mkyong.com/java/how-to-convert-bufferedimage-to-byte-in-java/
    // https://github.com/square/gifencoder
    val logger = StdOutLogger()
    try {
        val vfs = CommonVirtualFileSystem()
        GameEngine(
            gameOption = GameOption(
                128, 128, 4
            ),
            platform = GlfwPlatform(logger, vfs),
            vfs = vfs
        ).main()
    } catch (ex: Exception) {
        logger.error("TINY", ex) { "An unexpected exception occurred. The application will stop. It might be a bug in Tiny. If so, please report it."}
    }
}


