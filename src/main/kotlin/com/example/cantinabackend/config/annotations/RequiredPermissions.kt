package com.example.cantinabackend.config.annotations

enum class Permission(val groupId: String) {
    NORMAL_USER("9b250d90-5e20-49ce-bf8e-38a9e34b5c16"),
    ADMIN("6bdf85e5-7cb0-43e3-a62e-de1231eb0898"),
    MISSING_PERMISSION("no_permission");

    companion object {
        infix fun from(value: String): Permission = entries.firstOrNull { it.groupId == value } ?: MISSING_PERMISSION
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiredPermissions(
    val value: Array<Permission> = []
)