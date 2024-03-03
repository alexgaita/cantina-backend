package io.planer.shifts.web.errors

import jakarta.persistence.EntityNotFoundException
import kotlinx.serialization.Serializable
import org.hibernate.exception.SQLGrammarException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Serializable
data class ApiError(
    val status: HttpStatus,
    val errorKey: ErrorKey
)

@ControllerAdvice
class RestResourceEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [IllegalArgumentException::class])
    protected fun handleIllegalArgumentException(
        e: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorKey.INTERNAL_ERROR, request, e)

    @ExceptionHandler(value = [BadRequestException::class])
    protected fun handleWrongParameterValueException(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.BAD_REQUEST, ErrorKey.INVALID_PARAMETER, request, e)

    @ExceptionHandler(value = [UnauthorizedException::class, AuthenticationException::class])
    protected fun handleUnauthorizedException(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.UNAUTHORIZED, ErrorKey.UNAUTHORIZED, request, e)

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    protected fun handleHibernateConstraintException(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.BAD_REQUEST, ErrorKey.DATA_INTEGRITY_ERROR, request, e)

    @ExceptionHandler(value = [EntityNotFoundException::class])
    protected fun handleEntityNotFoundException(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.NOT_FOUND, ErrorKey.ENTITY_NOT_FOUND, request, e)

    @ExceptionHandler(value = [SQLGrammarException::class])
    protected fun handleSQLGrammarException(
        e: SQLGrammarException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorKey.INTERNAL_ERROR, request, e)

    @ExceptionHandler(value = [NoPermissionException::class])
    protected fun handleNoPermissionException(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.FORBIDDEN, ErrorKey.FORBIDDEN, request, e)

    override fun handleMissingServletRequestParameter(
        e: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.BAD_REQUEST, ErrorKey.MISSING_PARAMETER, request, e)

    // triggered when request is malformed
    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.BAD_REQUEST, ErrorKey.MALFORMED_PAYLOAD, request, e)

    // triggered when object @Valid validation fails
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> = createErrorResponse(HttpStatus.BAD_REQUEST, ErrorKey.VALIDATION_ERROR, request, e)

    private fun createErrorResponse(
        httpStatus: HttpStatus,
        errorKey: ErrorKey,
        request: WebRequest,
        cause: Throwable
    ): ResponseEntity<Any> {
        logger.error("Could not process request $request", cause)
        return ResponseEntity(
            ApiError(httpStatus, errorKey),
            HttpHeaders(),
            httpStatus
        )
    }

}