package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import io.planer.shifts.web.errors.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityAuthenticationService(
) {

    fun getUserId(): UUID {
        return UUID.fromString(getClaim("oid"))
    }

    fun getUserPermissions(): List<Permission> {
        val groups = getGroups()
        return groups.map { Permission.from(it) }.filter { it != Permission.MISSING_PERMISSION }
    }

    private fun getClaim(claim: String): String {
        val authentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken

        val token = authentication.token

        if (!token.hasClaim(claim)) {
            throw UnauthorizedException("User does not have the claim $claim assigned.")
        }

        return token.getClaimAsString(claim)
    }

    private fun getGroups(): Set<String> {
        val authentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken

        val token = authentication.token

        if (!token.hasClaim("groups")) {
            throw UnauthorizedException("User does not have any groups assigned.")
        }

        return token.getClaimAsStringList("groups").toSet()
    }

}