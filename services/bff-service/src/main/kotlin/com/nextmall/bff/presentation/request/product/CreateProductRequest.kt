package com.nextmall.bff.presentation.request.product

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank
    val name: String,

    @field:DecimalMin("0.01")
    val price: BigDecimal,

    @field:Min(0)
    val stock: Int,

    val category: String?,
)
