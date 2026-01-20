package com.nextmall.order.domain.exception

import com.nextmall.common.exception.base.BaseException

class OrderNotFoundException : BaseException(OrderErrorCode.ORDER_NOT_FOUND)
