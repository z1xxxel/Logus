package com.logus.tms_backend.controller;

import com.logus.tms_backend.DTO.CreateOrderRequest;
import com.logus.tms_backend.DTO.OrderFilterDTO;
import com.logus.tms_backend.DTO.OrderStatsDTO;
import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.model.UserRole;
import com.logus.tms_backend.repository.UserRepository;
import com.logus.tms_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    // 1. Создание заказа
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(
                request.getCustomerId(),
                request.getFromCity(),
                request.getFromAddress(),
                request.getToCity(),
                request.getToAddress(),
                request.getWeight(),
                request.getDeadline(),
                request.getCategoryId()
        );
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // 2. Получение заказа по ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // 3. Получение ВСЕХ заказов (удобно для Админа или Control Tower)
    // 3. Получение ВСЕХ заказов (удобно для Админа, Control Tower и Поддержки)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 4. Получение заказов конкретного Заказчика
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // 5. Получение заказов конкретного Перевозчика
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<List<Order>> getOrdersByCarrier(@PathVariable Long carrierId) {
        return ResponseEntity.ok(orderService.getOrdersByCarrier(carrierId));
    }

    // 6. Отмена заказа (доступно Заказчику или Админу)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    // 7. Завершение заказа (перевозчик отмечает доставку)
    @PutMapping("/{id}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long id,
                                               @RequestParam Long carrierId) {
        Order completedOrder = orderService.markDelivered(id, carrierId);
        return ResponseEntity.ok(completedOrder);
    }

    // 8. 🆕 Перевозчик подтверждает принятие заказа (ASSIGNED → IN_PROGRESS)
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long id,
                                              @RequestParam Long carrierId) {
        Order confirmedOrder = orderService.confirmOrder(id, carrierId);
        return ResponseEntity.ok(confirmedOrder);
    }

    // 9. История заказов заказчика (завершенные и отмененные)
    @GetMapping("/customer/{customerId}/history")
    public ResponseEntity<List<Order>> getCustomerHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerHistory(customerId));
    }

    // 10. История заказов перевозчика (завершенные и отмененные)
    @GetMapping("/carrier/{carrierId}/history")
    public ResponseEntity<List<Order>> getCarrierHistory(@PathVariable Long carrierId) {
        return ResponseEntity.ok(orderService.getCarrierHistory(carrierId));
    }

    // 11. Активные заказы заказчика
    @GetMapping("/customer/{customerId}/active")
    public ResponseEntity<List<Order>> getCustomerActiveOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerActiveOrders(customerId));
    }

    // 12. Активные заказы перевозчика
    @GetMapping("/carrier/{carrierId}/active")
    public ResponseEntity<List<Order>> getCarrierActiveOrders(@PathVariable Long carrierId) {
        return ResponseEntity.ok(orderService.getCarrierActiveOrders(carrierId));
    }

    // 13. Статистика заказчика
    @GetMapping("/customer/{customerId}/stats")
    public ResponseEntity<OrderStatsDTO> getCustomerStats(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerStats(customerId));
    }

    // 14. Статистика перевозчика
    @GetMapping("/carrier/{carrierId}/stats")
    public ResponseEntity<OrderStatsDTO> getCarrierStats(@PathVariable Long carrierId) {
        return ResponseEntity.ok(orderService.getCarrierStats(carrierId));
    }

    // 15. Поиск заказов с фильтрами (для перевозчика)
    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(
            @RequestParam(required = false) String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(required = false) BigDecimal minWeight,
            @RequestParam(required = false) BigDecimal maxWeight,
            @RequestParam(required = false) Boolean urgent,
            @RequestParam(required = false) Long categoryId) { // 🆕

        OrderFilterDTO filter = new OrderFilterDTO();
        filter.setFromCity(fromCity);
        filter.setToCity(toCity);
        filter.setMinWeight(minWeight);
        filter.setMaxWeight(maxWeight);
        filter.setUrgent(urgent);
        filter.setCategoryId(categoryId); // 🆕

        return ResponseEntity.ok(orderService.searchOrdersWithFilters(filter));
    }

    // 16. 🛠️ Обновить заказ (только Admin)
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @RequestBody CreateOrderRequest request,
                                             @RequestParam Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Админ не найден"));
        if (admin.getRole() != UserRole.Admin) {
            throw new RuntimeException("Доступ запрещен: только для администраторов");
        }

        Order updatedOrder = orderService.updateOrder(
                id,
                request.getFromCity(),
                request.getFromAddress(),
                request.getToCity(),
                request.getToAddress(),
                request.getWeight(),
                request.getDeadline(),
                request.getCategoryId()
        );
        return ResponseEntity.ok(updatedOrder);
    }

    // 17. 🛠️ Удалить заказ (только Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id,
                                            @RequestParam Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Админ не найден"));
        if (admin.getRole() != UserRole.Admin) {
            throw new RuntimeException("Доступ запрещен: только для администраторов");
        }

        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}