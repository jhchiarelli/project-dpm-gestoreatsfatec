package br.gov.sp.fatec.ifoodrestaurant.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private String dateOrder;
    private String paymentMethod;
    private Double totalOrder;
    private String userId;
    private String restaurantId;
    private List<OrderItem> items;

    public Order() {
    }

    public Order(String dateOrder, String paymentMethod, Double totalOrder) {
        this.dateOrder = dateOrder;
        this.paymentMethod = paymentMethod;
        this.totalOrder = totalOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Double totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}

class OrderItem {
    private String productId;
    private String productDescription;
    private Double amount;
    private Double totalPrice;

    public OrderItem() {
    }

    public OrderItem(String productId, String productDescription, Double amount, Double totalPrice) {
        this.productId = productId;
        this.productDescription = productDescription;
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
