package com.nextmall.bff.application.signup

interface SignUpFacade {
    suspend fun signUp(command: SignUpCommand): SignUpResult
}
