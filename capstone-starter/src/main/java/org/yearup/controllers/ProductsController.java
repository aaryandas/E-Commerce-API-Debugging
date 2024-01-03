package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController
{
    private ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> search(@RequestParam(name="cat", required = false) Integer categoryId,
                                          @RequestParam(name="minPrice", required = false) BigDecimal minPrice,
                                          @RequestParam(name="maxPrice", required = false) BigDecimal maxPrice,
                                          @RequestParam(name="color", required = false) String color
                                )
    {
        try {
            List<Product> itemFound = this.productDao.search(categoryId,minPrice,maxPrice,color);
            return new ResponseEntity<>(itemFound,HttpStatus.OK);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Product getById(@PathVariable int id){
        try {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else{
                return product;
            }
        } catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product){
        try{
            var createProduct = this.productDao.create(product);
            return new ResponseEntity<>(createProduct,HttpStatus.CREATED);

        }
        catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProduct(@Valid @RequestBody Product product, @PathVariable int id) {
        try{
            var foundProduct = this.productDao.getById(id);
            if(foundProduct == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                this.productDao.update(id,product);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id){
        try {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else{
                productDao.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
