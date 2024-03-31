package work.alsace.mapmanager.common.log

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.filter.AbstractFilter
import org.apache.logging.log4j.message.Message

class Log4JFilter : AbstractFilter() {
    override fun filter(event: LogEvent?): Filter.Result {
        var candidate: Message? = null
        if (event != null) {
            candidate = event.message
        }
        return validateMessage(candidate)
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Message?, t: Throwable?): Filter.Result {
        return validateMessage(msg)
    }

    override fun filter(
        logger: Logger?,
        level: Level?,
        marker: Marker?,
        msg: String?,
        vararg params: Any?
    ): Filter.Result {
        return validateMessage(msg)
    }

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Any?, t: Throwable?): Filter.Result {
        var candidate: String? = null
        if (msg != null) {
            candidate = msg.toString()
        }
        return validateMessage(candidate)
    }

    companion object {
        private fun validateMessage(message: Message?): Filter.Result {
            return if (message == null) {
                Filter.Result.NEUTRAL
            } else validateMessage(message.formattedMessage)
        }

        private fun validateMessage(message: String?): Filter.Result {
            return if (LogFilterHelper.isSensitiveCommand(message)) Filter.Result.DENY else Filter.Result.NEUTRAL
        }
    }
}
