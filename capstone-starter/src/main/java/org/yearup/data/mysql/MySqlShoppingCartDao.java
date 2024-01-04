package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource){super(dataSource);}

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String query = "SELECT p.*, ci.Quantity FROM ShoppingCartItems ci " +
                    "JOIN Products p ON ci.ProductId = p.ProductId " +
                    "WHERE ci.UserId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("ProductId");
                String name = rs.getString("Name");
                BigDecimal price = rs.getBigDecimal("Price");
                int categoryId = rs.getInt("CategoryId");
                String description = rs.getString("Description");
                String color = rs.getString("Color");
                int stock = rs.getInt("Stock");
                String imageUrl = rs.getString("ImageUrl");
                boolean featured = rs.getBoolean("Featured");
                int quantity = rs.getInt("Quantity");

                Product product = new Product(productId, name, price, categoryId, description, color, stock, featured, imageUrl);

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);

                cart.add(item);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public List<Product> getShoppingCartContents(int userId) {

        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM shopping_cart ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Extract product details from ResultSet
                int productId = rs.getInt("ProductId");
                String name = rs.getString("Name");
                BigDecimal price = rs.getBigDecimal("Price");
                int categoryId = rs.getInt("CategoryId");
                String description = rs.getString("Description");
                String color = rs.getString("Color");
                int stock = rs.getInt("Stock");
                String imageUrl = rs.getString("ImageUrl");
                boolean featured = rs.getBoolean("Featured");

                // Create Product object using full constructor
                Product product = new Product(productId, name, price, categoryId, description, color, stock, featured, imageUrl);

                // Add product to the list
                products.add(product);
            }
        } catch (SQLException e) {
            // Log and handle the exception
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void addProductToCart(int userId, Product product) {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, product.getProductId());
            ps.setInt(3, 1); // Assuming a default quantity of 1, adjust as needed

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShoppingCart create(ShoppingCart shoppingCart) {
        String sql = "INSERT INTO shopping_cart (product_id, quantity) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (ShoppingCartItem item : shoppingCart.getItems().values()) {
                ps.setInt(1, item.getProduct().getProductId());
                ps.setInt(2, item.getQuantity());

                ps.addBatch(); // Add to batch for bulk insertion
            }

            ps.executeBatch(); // Execute batch insert

        } catch (SQLException e) {
            // Log and handle the exception
            e.printStackTrace();
        }
        return shoppingCart;
    }


    @Override
    public void update(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);

            ps.executeUpdate();
        } catch (SQLException e) {
            // Log and handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public void clear(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Log and handle the exception
            e.printStackTrace();
        }
    }

}
