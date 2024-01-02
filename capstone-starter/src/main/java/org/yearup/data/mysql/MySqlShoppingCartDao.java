package org.yearup.data.mysql;

import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import java.util.List;

public class MySqlShoppingCartDao implements ShoppingCartDao {

    @Override
    public ShoppingCart getByUserId(int userId) {
        return null;
    }

    @Override
    public List<Product> getShoppingCartContents(int userId) {
        return null;
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
