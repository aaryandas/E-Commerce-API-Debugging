package org.yearup.controllers;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;
import java.sql.SQLException;
// convert this class to a REST controller
// only logged in users should have access to these actions

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    // each method in this controller requires a Principal object as a parameter

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal) {

        if(principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);

        } catch (UsernameNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error!");
        }
    }

    //POST METHOD
    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal) {

        if(principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            String userName = principal.getName();

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Product product = productDao.getById(productId);

            shoppingCartDao.addProductToCart(userId, product);

            ShoppingCart updatedCart = shoppingCartDao.getByUserId(userId);

            if(updatedCart == null || updatedCart.getItems().isEmpty()){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return updatedCart;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");
        }
    }

    // PUT METHOD
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductInCart(@PathVariable int productId, @RequestBody int quantity, Principal principal) {

        if(principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            String userName = principal.getName();

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Product product = productDao.getById(productId);

            shoppingCartDao.update(userId, productId, quantity);

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            if(cart == null || cart.getItems().isEmpty()){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return cart;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");
        }
    }
    //DELETE METHOD
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {

        if(principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            shoppingCartDao.clear(userId);

            return new ShoppingCart();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
