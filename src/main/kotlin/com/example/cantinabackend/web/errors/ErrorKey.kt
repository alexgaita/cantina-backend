package io.planer.shifts.web.errors

enum class ErrorKey(val key: String) {
    ENTITY_NOT_FOUND("entity.not.found"),
    INTERNAL_ERROR("internal.error"),
    MALFORMED_PAYLOAD("malformed.payload"),
    MISSING_PARAMETER("missing.parameter"),
    VALIDATION_ERROR("validation.error"),
    DATA_INTEGRITY_ERROR("data.integrity.error"),
    UNAUTHORIZED("unauthorized"),
    FORBIDDEN("forbidden"),
    INVALID_PARAMETER("invalid.parameter"),
}