package work.alsace.mapmanager.log

import com.google.common.annotations.VisibleForTesting
import java.util.*

object LogFilterHelper {
    @VisibleForTesting
    val COMMANDS_TO_SKIP = withAndWithoutPrefix(
        "/world download", "/world changepassword ", "/mapadmin.md changepassword "
    )

    fun isSensitiveCommand(message: String?): Boolean {
        if (message == null) {
            return false
        }
        val msg = message.lowercase(Locale.getDefault())
        return msg.contains("issued server command:") && containsAny(msg, COMMANDS_TO_SKIP)
    }

    private fun withAndWithoutPrefix(vararg commands: String?): MutableList<String?>? {
        val commandList: MutableList<String?> = ArrayList(commands.size shl 1)
        for (command in commands) {
            commandList.add(command)
            commandList.add("/mapmanager:" + (command?.substring(1)))
        }
        return Collections.unmodifiableList(commandList)
    }

    fun containsAny(str: String?, pieces: Iterable<String?>?): Boolean {
        if (str == null) {
            return false
        }
        if (pieces != null) {
            for (piece in pieces) {
                if (piece != null && str.contains(piece)) {
                    return true
                }
            }
        }
        return false
    }
}
