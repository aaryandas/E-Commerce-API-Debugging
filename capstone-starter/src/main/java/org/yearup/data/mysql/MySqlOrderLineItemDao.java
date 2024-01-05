package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {

    @Autowired
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public OrderLineItem create(OrderLineItem orderLineItem) throws SQLException {
        String sql = "INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, orderLineItem.getOrderId());
            statement.setInt(2, orderLineItem.getProductId());
            statement.setBigDecimal(3, orderLineItem.getSalesPrice());
            statement.setInt(4, orderLineItem.getQuantity());
            statement.setBigDecimal(5, orderLineItem.getDiscount());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order line item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderLineItem.setOrderLineItemId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order line item failed, no ID obtained.");
                }
            }
        }
        return orderLineItem;
    }

    @Override
    public List<OrderLineItem> getOrderLineItemsByOrderId(int orderId) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        String sql = "SELECT * FROM order_line_items WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    OrderLineItem item = mapRowToOrderLineItem(resultSet);
                    orderLineItems.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching order line items for order ID: " + orderId, e);
        }
        return orderLineItems;
    }

    private OrderLineItem mapRowToOrderLineItem(ResultSet resultSet) throws SQLException {
        OrderLineItem item = new OrderLineItem();
        item.setOrderLineItemId(resultSet.getInt("order_line_item_id"));
        item.setOrderId(resultSet.getInt("order_id"));
        item.setProductId(resultSet.getInt("product_id"));
        item.setSalesPrice(resultSet.getBigDecimal("sales_price"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setDiscount(resultSet.getBigDecimal("discount"));

        return item;
    }
}
