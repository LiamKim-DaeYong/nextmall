package com.nextmall.auth.port.input.logout

interface LogoutCommand {
    fun logout(refreshToken: String)
}
