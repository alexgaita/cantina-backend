package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Cartela
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CartelaRepository : JpaRepository<Cartela, UUID> {

    @Query(
        """
        SELECT c
        FROM Cartela c
        WHERE c.user.id = :userId
    """
    )
    fun findAllByUser(userId: UUID): List<Cartela>

    @Query(
        """
        SELECT c
        FROM Cartela c
        WHERE c.id IN :ids
        AND c.user.id = :userId
    """
    )
    fun findAllByIdsAndUser(ids: List<UUID>, userId: UUID): List<Cartela>

}