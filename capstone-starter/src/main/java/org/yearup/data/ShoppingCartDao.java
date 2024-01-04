package org.yearup.data;

import org.springframework.stereotype.Repository;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import java.util.List;

@Repository
public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here

    List<Product> getShoppingCartContents (int userId);

    void addProductToCart(int userId, Product product);

    ShoppingCart create(ShoppingCart shoppingCart);
    void update(int userId, int productId, int quantity);
    void clear(int userId);

}
