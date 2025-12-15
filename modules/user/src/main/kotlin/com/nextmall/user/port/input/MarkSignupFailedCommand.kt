package com.nextmall.user.port.input

interface MarkSignupFailedCommand {
    fun markFailed(userId: Long)
}
