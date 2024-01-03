package org.yearup.data.mysql;

import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

public class MySqlShoppingCartDao implements ShoppingCartDao {

    @Override
    public ShoppingCart getByUserId(int userId) {
        String query = "SELECT * FROM shopping_cart WHERE user_id = ?";

        return null;
    }

    @Override
    public List<Product> getShoppingCartContents(int userId) {
        return null;
    }

    @Override
    public void addProductToCart(int userId, Product product) {

    }

    @Override
    public ShoppingCart create(ShoppingCart shoppingCart) {
        return null;
    }

    @Override
    public void update(int userId, int productId, int quantity) {

    }

    @Override
    public void clear(int userId) {

    }

}
