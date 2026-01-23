package com.nextmall.checkout.domain.exception

import com.nextmall.common.exception.base.BaseException

class CheckoutNotFoundException : BaseException(CheckoutErrorCode.CHECKOUT_NOT_FOUND)
