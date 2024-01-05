package org.yearup.data;

import org.yearup.models.OrderLineItem;

import java.sql.SQLException;
import java.util.List;

public interface OrderLineItemDao {

    OrderLineItem create(OrderLineItem orderLineItem) throws SQLException;

    List<OrderLineItem> getOrderLineItemsByOrderId(int orderId);
}
