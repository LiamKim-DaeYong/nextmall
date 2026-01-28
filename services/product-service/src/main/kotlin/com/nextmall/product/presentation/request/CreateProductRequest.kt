package com.nextmall.product.presentation.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank
    val name: String,

    @field:Size(max = 2000)
    val description: String? = null,

    @field:DecimalMin("0.01")
    val price: BigDecimal,

    @field:Min(0)
    val stock: Int,

    val category: String? = null,
)
