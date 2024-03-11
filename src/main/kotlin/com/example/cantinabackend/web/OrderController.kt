package com.example.cantinabackend.web

import com.example.cantinabackend.web.swagger.IOrderController
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
@Validated
class OrderController(
) : IOrderController {

}