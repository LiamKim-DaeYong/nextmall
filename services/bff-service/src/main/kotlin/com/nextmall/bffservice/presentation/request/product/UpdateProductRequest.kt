package com.nextmall.bffservice.presentation.request.product

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UpdateProductRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val price: String,
    @field:Min(0)
    val stock: Int,
    val category: String?,
)
