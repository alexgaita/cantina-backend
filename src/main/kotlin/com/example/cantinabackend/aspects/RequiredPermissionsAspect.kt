package com.example.cantinabackend.aspects

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import io.planer.shifts.web.errors.NoPermissionException
import io.planer.shifts.web.errors.UnauthorizedException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Aspect
@Component
class RequiredPermissionsAspect {

    @Before("@annotation(requiredPermissions)")
    fun interceptAnnotation(joinPoint: JoinPoint, requiredPermissions: RequiredPermissions) {

        val authentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken

        val token = authentication.token
        
        if (!token.hasClaim("groups")) {
            throw UnauthorizedException("User does not have any groups assigned.")
        }

        val userGroups =
            token.getClaimAsStringList("groups").toSet()

        val missingPermissions = requiredPermissions.value.map { it.groupId } - userGroups

        if (missingPermissions.isNotEmpty()) {
            throw NoPermissionException(
                "User does not have sufficient permissions: ${
                    missingPermissions.map {
                        Permission.from(
                            it
                        ).name
                    }
                }"
            )
        }

    }
}