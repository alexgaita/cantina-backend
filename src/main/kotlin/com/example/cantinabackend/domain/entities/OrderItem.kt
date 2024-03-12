package com.example.cantinabackend.domain.entities

import jakarta.persistence.*

@Entity
class OrderItem(

    @Column(columnDefinition = "DECIMAL(4,2)")
    var price: Double,

    @Column
    var quantity: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_name")
    var menuItem: MenuItem,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cantina_order_id")
    var order: Order,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0

}