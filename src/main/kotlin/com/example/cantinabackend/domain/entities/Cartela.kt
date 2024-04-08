package com.example.cantinabackend.domain.entities

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
class Cartela(

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: UUID,

    @Column(columnDefinition = "DECIMAL(4,2)")
    var value: Double,

    @Column
    var isActive: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order?

)