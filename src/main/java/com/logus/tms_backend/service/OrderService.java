package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.OrderFilterDTO;
import com.logus.tms_backend.DTO.OrderStatsDTO;
import com.logus.tms_backend.DTO.PricePredictionRequest;
import com.logus.tms_backend.DTO.PricePredictionResponse;
import com.logus.tms_backend.model.CargoCategory;
import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.model.OrderStatus;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.repository.CargoCategoryRepository;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CargoCategoryRepository categoryRepository;

    @Autowired
    private PricePredictionService predictionService;

    // 1. Создание заказа (адаптировано под ваш CreateOrderRequest / параметры контроллера)
    @Transactional
    public Order createOrder(Long customerId, String fromCity, String fromAddress,
                             String toCity, String toAddress, BigDecimal weight,
                             LocalDateTime deadline, Long categoryId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setFromCity(fromCity);
        order.setFromAddress(fromAddress);
        order.setToCity(toCity);
        order.setToAddress(toAddress);
        order.setWeight(weight != null ? weight : BigDecimal.ZERO);
        order.setDeadline(deadline);
        order.setStatus(OrderStatus.NEW);

        if (categoryId != null) {
            CargoCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория груза не найдена"));
            order.setCategory(category);
        }

        // 🆕 ML-предсказание цены
        try {
            PricePredictionRequest predReq = new PricePredictionRequest();
            predReq.setFromCity(order.getFromCity());
            predReq.setToCity(order.getToCity());
            predReq.setWeight(order.getWeight() != null ? order.getWeight().doubleValue() : null);
            predReq.setCategoryId(order.getCategory() != null ? order.getCategory().getId() : null);
            predReq.setDeadline(order.getDeadline());

            PricePredictionResponse prediction = predictionService.predictPrice(predReq);
            order.setPredictedPrice(prediction.getPredictedPrice());
        } catch (Exception e) {
            System.err.println("Price prediction failed: " + e.getMessage());
        }

        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByCustomerWithDetails(customer);
    }

    public List<Order> getOrdersByCarrier(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByCarrierWithDetails(carrier);
    }

    // 6. Отмена заказа (адаптировано под контроллер, который передает только id)
    @Transactional
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя отменить завершенный заказ");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // 7. Завершение заказа (переименовано в markDelivered, как в контроллере)
    @Transactional
    public Order markDelivered(Long id, Long carrierId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Заказ должен быть в статусе IN_PROGRESS для завершения");
        }

        if (order.getCarrier() == null || !order.getCarrier().getId().equals(carrierId)) {
            throw new RuntimeException("Перевозчик не назначен на этот заказ");
        }

        order.setStatus(OrderStatus.COMPLETED);

        User carrier = order.getCarrier();
        carrier.setCompletedOrders(carrier.getCompletedOrders() != null ? carrier.getCompletedOrders() + 1 : 1);
        userRepository.save(carrier);

        return orderRepository.save(order);
    }

    // 8. Подтверждение заказа
    @Transactional
    public Order confirmOrder(Long id, Long carrierId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new RuntimeException("Заказ должен быть в статусе ASSIGNED для подтверждения");
        }

        if (order.getCarrier() == null || !order.getCarrier().getId().equals(carrierId)) {
            throw new RuntimeException("Перевозчик не назначен на этот заказ");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    // 9-12. История и активные заказы (используют ваши @Query из репозитория)
    public List<Order> getCustomerHistory(Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findHistoryByCustomer(customer);
    }

    public List<Order> getCarrierHistory(Long carrierId) {
        User carrier = userRepository.findById(carrierId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findHistoryByCarrier(carrier);
    }

    public List<Order> getCustomerActiveOrders(Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findActiveByCustomer(customer);
    }

    public List<Order> getCarrierActiveOrders(Long carrierId) {
        User carrier = userRepository.findById(carrierId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findActiveByCarrier(carrier);
    }

    // 13-14. Статистика (возвращает OrderStatsDTO, как ждет контроллер)
    public OrderStatsDTO getCustomerStats(Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<Order> orders = orderRepository.findByCustomerWithDetails(customer);

        return OrderStatsDTO.builder()
                .totalOrders((long) orders.size())
                .completedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count())
                .activeOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.ASSIGNED || o.getStatus() == OrderStatus.IN_PROGRESS).count())
                .cancelledOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
                .build();
    }

    public OrderStatsDTO getCarrierStats(Long carrierId) {
        User carrier = userRepository.findById(carrierId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<Order> orders = orderRepository.findByCarrierWithDetails(carrier);

        return OrderStatsDTO.builder()
                .totalOrders((long) orders.size())
                .completedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count())
                .activeOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.ASSIGNED || o.getStatus() == OrderStatus.IN_PROGRESS).count())
                .cancelledOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
                .build();
    }

    @Transactional
    public Order assignCarrier(Long orderId, Long carrierId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Заказ должен быть в статусе NEW для назначения перевозчика");
        }

        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));

        // Назначаем перевозчика и меняем статус
        order.setCarrier(carrier);
        order.setStatus(OrderStatus.ASSIGNED);

        return orderRepository.save(order);
    }
    // 15. Поиск с фильтрами (адаптировано под OrderFilterDTO)
    public List<Order> searchOrdersWithFilters(OrderFilterDTO filter) {
        LocalDateTime deadlineBefore = (filter.getUrgent() != null && filter.getUrgent()) ? LocalDateTime.now().plusDays(3) : null;

        return orderRepository.findOrdersWithFilters(
                filter.getFromCity(),
                filter.getToCity(),
                filter.getMinWeight(),
                filter.getMaxWeight(),
                deadlineBefore,
                filter.getCategoryId()
        );
    }

    // 16. Обновление заказа (адаптировано под параметры контроллера)
    @Transactional
    public Order updateOrder(Long id, String fromCity, String fromAddress, String toCity,
                             String toAddress, BigDecimal weight, LocalDateTime deadline, Long categoryId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя изменить завершенный заказ");
        }

        order.setFromCity(fromCity);
        order.setFromAddress(fromAddress);
        order.setToCity(toCity);
        order.setToAddress(toAddress);
        order.setWeight(weight != null ? weight : BigDecimal.ZERO);
        order.setDeadline(deadline);

        if (categoryId != null) {
            CargoCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория груза не найдена"));
            order.setCategory(category);
        }

        return orderRepository.save(order);
    }

    // 17. Удаление заказа
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.IN_PROGRESS || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя удалить активный или завершенный заказ");
        }

        orderRepository.delete(order);
    }
}