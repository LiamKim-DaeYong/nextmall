package com.nextmall.order.domain.exception

import com.nextmall.common.exception.base.BaseException
import com.nextmall.order.application.exception.OrderErrorCode

class OrderNotFoundException : BaseException(OrderErrorCode.ORDER_NOT_FOUND)
