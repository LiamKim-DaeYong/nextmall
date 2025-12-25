// package com.nextmall.apigateway.exception
//
// import com.nextmall.common.exception.base.BaseException
// import org.springframework.http.ResponseEntity
// import org.springframework.web.bind.annotation.ExceptionHandler
// import org.springframework.web.bind.annotation.RestControllerAdvice
//
// @RestControllerAdvice
// class GlobalExceptionHandler(
//    private val httpStatusMapper: HttpStatusMapper,
// ) {
//    @ExceptionHandler(BaseException::class)
//    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
//        val status = httpStatusMapper.map(ex.errorCode)
//
//        return ResponseEntity
//            .status(status)
//            .body(
//                ErrorResponse(
//                    code = ex.errorCode.code,
//                    message = ex.errorCode.message,
//                ),
//            )
//    }
// }
