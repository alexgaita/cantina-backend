package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AddressRepository : JpaRepository<Address, Int> {

    @Modifying
    @Query("UPDATE Address a SET a.isCurrent = FALSE where a.isCurrent = TRUE AND a.user.id = :userId")
    fun setAllAddressesNotCurrent(userId: UUID)

}