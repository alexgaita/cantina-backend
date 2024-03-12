package com.example.cantinabackend.domain.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

enum class OrderStatus {
    CREATED,
    PAYMENT_REQUIRED,
    IN_PROGRESS,
    DELIVERED
}

@Entity(name = "cantina_order")
class Order(

    @Column(columnDefinition = "DECIMAL(4,2)")
    var totalPrice: Double = 0.0,

    @Column
    var description: String?,

    @Column
    var useSilverware: Boolean = false,

    @Column
    var paymentId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.CREATED,

    @Column
    @CreationTimestamp
    var createdAt: Instant? = null,

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var orderItems: MutableList<OrderItem> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    var address: Address

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0

}