package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.data.OrderDao;
import org.yearup.data.OrderLineItemDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private static final BigDecimal SHIPPING_COST = new BigDecimal("4.99");

    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final OrderLineItemDao orderLineItemDao;

    @Autowired
    public OrdersController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao, OrderLineItemDao orderLineItemDao) {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.orderLineItemDao = orderLineItemDao;
    }

    @PostMapping
    public Order createOrder(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);
            if (shoppingCart == null || shoppingCart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setDate(new Date(System.currentTimeMillis()));
            order.setShippingAmount(SHIPPING_COST);
            orderDao.create(order);

            List<OrderLineItem> orderLineItems = new ArrayList<>();
            for (ShoppingCartItem item : shoppingCart.getItems().values()) {
                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setOrderId(order.getOrderId());
                orderLineItem.setProductId(item.getProductId());
                orderLineItem.setSalesPrice(item.getLineTotal());
                orderLineItem.setQuantity(item.getQuantity());
                orderLineItem.setDiscount(item.getDiscountPercent());

                orderLineItems.add(orderLineItemDao.create(orderLineItem));

            }
            Order completeOrder = orderDao.getById(order.getOrderId());
            List<OrderLineItem> completeOrderLineItems = orderLineItemDao.getOrderLineItemsByOrderId(completeOrder.getOrderId());
            completeOrder.setOrderLineItems(completeOrderLineItems);

            shoppingCartDao.clear(userId);

            return completeOrder;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating order", exception);
        }
    }
}
