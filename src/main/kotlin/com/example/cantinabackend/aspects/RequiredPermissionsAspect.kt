package com.example.cantinabackend.aspects

import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.services.SecurityAuthenticationService
import io.planer.shifts.web.errors.NoPermissionException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class RequiredPermissionsAspect(
    private val securityAuthenticationService: SecurityAuthenticationService
) {

    @Before("@annotation(requiredPermissions)")
    fun interceptAnnotation(joinPoint: JoinPoint, requiredPermissions: RequiredPermissions) {

        val userPermissions = securityAuthenticationService.getUserPermissions().toSet()

        val missingPermissions = requiredPermissions.value.toSet() - userPermissions

        if (missingPermissions.isNotEmpty()) {
            throw NoPermissionException(
                "User does not have sufficient permissions: ${
                    missingPermissions.map {
                        it.name
                    }
                }"
            )
        }

    }
}