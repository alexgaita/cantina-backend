package com.example.cantinabackend.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
class User(

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: UUID,

    @Column
    var phoneNumber: String?,

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    val addresses: MutableSet<Address> = mutableSetOf()

)