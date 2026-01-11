package com.nextmall.productservice.presentation.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank
    val name: String,

    @field:NotNull
    @field:DecimalMin("0.0")
    val price: BigDecimal,

    @field:NotNull
    @field:Min(0)
    val stock: Int,

    val category: String? = null
)
