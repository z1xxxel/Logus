package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.OrderFilterDTO;
import com.logus.tms_backend.DTO.OrderStatsDTO;
import com.logus.tms_backend.model.*;
import com.logus.tms_backend.repository.BidRepository;
import com.logus.tms_backend.repository.CargoCategoryRepository;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final CargoCategoryRepository cargoCategoryRepository;

    /**
     * 1. Создание заказа (только Customer)
     */
    @Transactional
    public Order createOrder(Long customerId, String fromCity, String fromAddress,
                             String toCity, String toAddress,
                             BigDecimal weight, LocalDateTime deadline,
                             Long categoryId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));

        if (customer.getRole() != UserRole.Customer) {
            throw new RuntimeException("Только заказчик может создавать заказы");
        }

        // Получаем категорию груза (если указана)
        CargoCategory category = null;
        if (categoryId != null) {
            category = cargoCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория груза не найдена"));
        }

        Order order = Order.builder()
                .fromCity(fromCity)
                .fromAddress(fromAddress)
                .toCity(toCity)
                .toAddress(toAddress)
                .weight(weight)
                .deadline(deadline)
                .category(category)
                .customer(customer)
                .status(OrderStatus.NEW)
                .build();

        return orderRepository.save(order);
    }

    /**
     * 2. Получение заказа по ID
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    /**
     * 3. Получение всех заказов (для Админа или Control Tower)
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    /**
     * 4. Получение заказов конкретного Заказчика
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByCustomerWithDetails(customer);
    }

    /**
     * 5. Получение заказов конкретного Перевозчика
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCarrier(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByCarrierWithDetails(carrier);
    }

    /**
     * 6. Получение заказов по статусу (для фильтрации)
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusWithDetails(status);
    }

    /**
     * 7. Назначение перевозчика на заказ (принятие предложения)
     * Переводит заказ из NEW в ASSIGNED
     */
    @Transactional
    public Order assignCarrier(Long orderId, Long bidId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Заказ уже в работе или завершён");
        }

        Bid bid = bidRepository.findByIdWithDetails(bidId)
                .orElseThrow(() -> new RuntimeException("Предложение не найдено"));

        if (!bid.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Предложение не относится к этому заказу");
        }

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new RuntimeException("Предложение уже обработано");
        }

        order.setCarrier(bid.getCarrier());
        order.setStatus(OrderStatus.ASSIGNED);

        List<Bid> otherBids = bidRepository.findByOrderIdAndStatusWithDetails(orderId, BidStatus.PENDING);
        for (Bid otherBid : otherBids) {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setStatus(BidStatus.REJECTED);
            }
        }
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        return orderRepository.save(order);
    }

    /**
     * 8. Завершение заказа администратором (для исключительных случаев)
     */
    @Transactional
    public Order completeOrderAdmin(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Заказ не в процессе выполнения");
        }

        User carrier = order.getCarrier();
        if (carrier == null) {
            throw new RuntimeException("Перевозчик не назначен");
        }

        Integer completed = carrier.getCompletedOrders() != null ? carrier.getCompletedOrders() : 0;
        carrier.setCompletedOrders(completed + 1);
        userRepository.save(carrier);

        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    /**
     * 9. Отмена заказа (только для Customer или Admin)
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Завершённый заказ нельзя отменить");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * 10. Получить историю завершенных заказов заказчика
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerHistory(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));
        return orderRepository.findHistoryByCustomer(customer);
    }

    /**
     * 11. Получить историю завершенных заказов перевозчика
     */
    @Transactional(readOnly = true)
    public List<Order> getCarrierHistory(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));
        return orderRepository.findHistoryByCarrier(carrier);
    }

    /**
     * 12. Получить активные заказы заказчика
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerActiveOrders(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));
        return orderRepository.findActiveByCustomer(customer);
    }

    /**
     * 13. Получить активные заказы перевозчика
     */
    @Transactional(readOnly = true)
    public List<Order> getCarrierActiveOrders(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));
        return orderRepository.findActiveByCarrier(carrier);
    }

    /**
     * 14. Получить статистику по заказам заказчика
     */
    @Transactional(readOnly = true)
    public OrderStatsDTO getCustomerStats(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));

        List<Order> allOrders = orderRepository.findByCustomerWithDetails(customer);

        long total = allOrders.size();
        long completed = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long active = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW
                        || o.getStatus() == OrderStatus.ASSIGNED
                        || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
        long cancelled = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        BigDecimal totalSpent = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total > 0
                ? totalSpent.divide(BigDecimal.valueOf(total), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new OrderStatsDTO(total, completed, active, cancelled, totalSpent, average);
    }

    /**
     * 15. Получить статистику по заказам перевозчика
     */
    @Transactional(readOnly = true)
    public OrderStatsDTO getCarrierStats(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));

        List<Order> allOrders = orderRepository.findByCarrierWithDetails(carrier);

        long total = allOrders.size();
        long completed = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long active = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.ASSIGNED
                        || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
        long cancelled = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        BigDecimal totalEarned = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total > 0
                ? totalEarned.divide(BigDecimal.valueOf(total), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new OrderStatsDTO(total, completed, active, cancelled, totalEarned, average);
    }

    /**
     * 16. Поиск заказов с фильтрами (для перевозчика)
     */
    @Transactional(readOnly = true)
    public List<Order> searchOrdersWithFilters(OrderFilterDTO filter) {
        LocalDateTime deadlineBefore = null;
        if (filter.getUrgent() != null && filter.getUrgent()) {
            deadlineBefore = LocalDateTime.now().plusDays(3);
        }

        return orderRepository.findOrdersWithFilters(
                filter.getFromCity(),
                filter.getToCity(),
                filter.getMinWeight(),
                filter.getMaxWeight(),
                deadlineBefore,
                filter.getCategoryId() // 🆕
        );
    }

    /**
     * 17. 🛠️ Обновить заказ (только Admin)
     */
    @Transactional
    public Order updateOrder(Long orderId, String fromCity, String fromAddress,
                             String toCity, String toAddress,
                             BigDecimal weight, LocalDateTime deadline,
                             Long categoryId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя изменить завершенный заказ");
        }

        CargoCategory category = null;
        if (categoryId != null) {
            category = cargoCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория груза не найдена"));
        }

        order.setFromCity(fromCity);
        order.setFromAddress(fromAddress);
        order.setToCity(toCity);
        order.setToAddress(toAddress);
        order.setWeight(weight);
        order.setDeadline(deadline);
        order.setCategory(category);

        return orderRepository.save(order);
    }

    /**
     * 18. 🛠️ Удалить заказ (только Admin)
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.IN_PROGRESS || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя удалить заказ, который уже выполняется или завершен");
        }

        List<Bid> bids = bidRepository.findByOrderId(orderId);
        bidRepository.deleteAll(bids);

        orderRepository.delete(order);
    }

    /**
     * 19. 🆕 Перевозчик подтверждает принятие заказа
     * Переводит заказ из ASSIGNED в IN_PROGRESS
     */
    @Transactional
    public Order confirmOrder(Long orderId, Long carrierId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new RuntimeException("Заказ не в статусе назначения");
        }

        if (order.getCarrier() == null || !order.getCarrier().getId().equals(carrierId)) {
            throw new RuntimeException("Только назначенный перевозчик может подтвердить заказ");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    /**
     * 20. 🆕 Перевозчик отмечает доставку
     * Переводит заказ из IN_PROGRESS в COMPLETED
     */
    @Transactional
    public Order markDelivered(Long orderId, Long carrierId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Заказ не в процессе выполнения");
        }

        if (order.getCarrier() == null || !order.getCarrier().getId().equals(carrierId)) {
            throw new RuntimeException("Только назначенный перевозчик может отметить доставку");
        }

        User carrier = order.getCarrier();
        Integer completed = carrier.getCompletedOrders() != null ? carrier.getCompletedOrders() : 0;
        carrier.setCompletedOrders(completed + 1);
        userRepository.save(carrier);

        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }
}