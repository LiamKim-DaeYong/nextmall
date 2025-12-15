package com.nextmall.auth.port.input.token

import com.nextmall.auth.application.command.token.TokenResult

interface IssueTokenCommand {
    fun issue(userId: Long): TokenResult
}
