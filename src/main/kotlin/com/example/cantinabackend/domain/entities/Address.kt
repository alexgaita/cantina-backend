package com.example.cantinabackend.domain.entities

import jakarta.persistence.*

@Entity
class Address(

    @Column
    var value: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
}