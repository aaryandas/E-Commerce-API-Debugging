package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;
// convert this class to a REST controller
// only logged in users should have access to these actions

public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    // each method in this controller requires a Principal object as a parameter

    public ShoppingCart getCart(Principal principal) {
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart

            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");
        }
    }

    //POST METHOD
    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(@PathVariable Long productId, Principal principal) {
        try {
            String userName = principal.getName();

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Product product = productDao.getProductById(productId);

            shoppingCartDao.addProductToCart(userId, product);

            return shoppingCartDao.getCartByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");
        }
    }

    // PUT METHOD
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductInCart(@PathVariable Long productId, @RequestBody int quantity, Principal principal) {
        try {
            String userName = principal.getName();

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Product product = productDao.getById(productId);

            shoppingCartDao.updateProductInCart(userId, product, quantity);

            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");
        }
    }
    //DELETE METHOD
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {
        try {
            String userName = principal.getName();

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            shoppingCartDao.clearCart(userId);

            return shoppingCartDao.getCartByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
