package com.nextmall.user.domain.exception

class DuplicateEmailException(
    email: String,
) : RuntimeException("Duplicate email")
