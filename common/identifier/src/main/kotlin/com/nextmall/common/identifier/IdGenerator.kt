package com.nextmall.common.identifier

/**
 * Common interface for generating unique identifiers.
 */
fun interface IdGenerator {
    fun generate(): Long
}
