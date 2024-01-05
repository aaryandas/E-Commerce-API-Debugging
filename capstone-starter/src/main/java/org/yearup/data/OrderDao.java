package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;

import java.sql.SQLException;

public interface OrderDao {


    Order create(Order order) throws SQLException;

    Order getById(int orderId);
}
