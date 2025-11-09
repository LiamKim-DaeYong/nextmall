package com.nextmall.common.identifier

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple Snowflake-like ID generator.
 * (timestamp + node + sequence)
 */
@Component
class SnowflakeIdGenerator(
    private val nodeId: Long = 1L,
) : IdGenerator {
    init {
        require(nodeId in 0..MAX_NODE_ID) {
            "nodeId must be between 0 and $MAX_NODE_ID, but was $nodeId"
        }
    }

    private val sequence = AtomicLong(0)
    private var lastTimestamp = -1L

    override fun generate(): Long {
        var timestamp = currentTime()
        synchronized(this) {
            if (timestamp == lastTimestamp) {
                val seq = (sequence.incrementAndGet()) and MAX_SEQUENCE
                if (seq == 0L) {
                    timestamp = waitNextMillis(lastTimestamp)
                }
            } else {
                sequence.set(0)
            }
            lastTimestamp = timestamp
            return ((timestamp - EPOCH) shl TIMESTAMP_SHIFT) or
                (nodeId shl NODE_SHIFT) or
                sequence.get()
        }
    }

    private fun waitNextMillis(lastTimestamp: Long): Long {
        var ts = currentTime()
        while (ts <= lastTimestamp) {
            ts = currentTime()
        }
        return ts
    }

    private fun currentTime(): Long = Instant.now().toEpochMilli()

    companion object {
        private const val NODE_BITS = 10
        private const val SEQUENCE_BITS = 12
        private const val MAX_NODE_ID = (1L shl NODE_BITS) - 1
        private const val NODE_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_SHIFT = NODE_BITS + SEQUENCE_BITS
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1
        private const val EPOCH = 1730000000000L // custom epoch (2024-10 approx)
    }
}
