package com.nextmall.product.domain.exception

import com.nextmall.common.exception.base.BaseException
import com.nextmall.product.application.exception.ProductErrorCode

class ProductNotFoundException : BaseException(ProductErrorCode.PRODUCT_NOT_FOUND)
