package com.nextmall.product.domain.exception

import com.nextmall.common.exception.base.BaseException

class ProductNotFoundException : BaseException(ProductErrorCode.PRODUCT_NOT_FOUND)
