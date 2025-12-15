package com.nextmall.user.port.input

interface ActivateUserCommand {
    fun activate(userId: Long)
}
