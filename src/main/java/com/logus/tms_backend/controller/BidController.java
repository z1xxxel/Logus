package com.logus.tms_backend.controller;

import com.logus.tms_backend.DTO.BidScoreDTO;
import com.logus.tms_backend.DTO.CreateBidRequest;
import com.logus.tms_backend.model.Bid;
import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.service.BidService;
import com.logus.tms_backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final OrderService orderService; // Нужен для acceptBid

    /**
     * POST /api/bids – создание нового предложения (только для перевозчика)
     */
    @PostMapping
    public ResponseEntity<Bid> createBid(@RequestBody CreateBidRequest request) {
        Bid createdBid = bidService.createBid(
                request.getOrderId(),
                request.getCarrierId(),
                request.getPrice(),
                request.getEstimatedDays(),
                request.getComment()
        );
        return new ResponseEntity<>(createdBid, HttpStatus.CREATED);
    }

    /**
     * 🏆 ГЛАВНЫЙ ЭНДПОИНТ: Умное ранжирование через параметр запроса
     * GET /api/bids/orders/recommendations?orderId=1
     */
    @GetMapping("/orders/recommendations")
    public ResponseEntity<List<BidScoreDTO>> getRankedBids(@RequestParam Long orderId) {
        List<BidScoreDTO> rankedBids = bidService.getRankedBidsForOrder(orderId);
        return ResponseEntity.ok(rankedBids);
    }

    /**
     * GET /api/bids/order/{orderId} – все предложения для заказа (без ранжирования)
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Bid>> getBidsForOrder(@PathVariable Long orderId) {
        List<Bid> bids = bidService.getBidsByOrder(orderId);
        return ResponseEntity.ok(bids);
    }

    /**
     * PUT /api/bids/{bidId}/accept – принятие предложения заказчиком
     */
    @PutMapping("/{bidId}/accept")
    @Transactional // Добавляем транзакцию для безопасности
    public ResponseEntity<Order> acceptBid(@PathVariable Long bidId) {
        // Находим предложение
        Bid bid = bidService.getBidById(bidId);

        // ✅ ИСПРАВЛЕНО: передаем orderId и carrierId (а не bidId)
        Order updatedOrder = orderService.assignCarrier(bid.getOrder().getId(), bid.getCarrier().getId());

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * PUT /api/bids/{bidId}/reject – отклонение предложения заказчиком
     */
    @PutMapping("/{bidId}/reject")
    public ResponseEntity<Void> rejectBid(@PathVariable Long bidId) {
        bidService.rejectBid(bidId);
        return ResponseEntity.noContent().build();
    }
    /**
     * 🆕 GET /api/bids — получить все предложения в системе (для админа)
     */
    @GetMapping
    public ResponseEntity<List<Bid>> getAllBids() {
        return ResponseEntity.ok(bidService.getAllBids());
    }

    /**
     * 🆕 GET /api/bids/carrier/{carrierId} — получить все предложения конкретного перевозчика
     */
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<List<Bid>> getBidsByCarrier(@PathVariable Long carrierId) {
        return ResponseEntity.ok(bidService.getBidsByCarrier(carrierId));
    }

}