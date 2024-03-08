package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Int> {

}