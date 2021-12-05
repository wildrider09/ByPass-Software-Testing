package com.nitesh.biddingPlatform.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.nitesh.biddingPlatform.api.UserResource;
import com.nitesh.biddingPlatform.dao.ProductDao;
import com.nitesh.biddingPlatform.dao.UserDao;
import com.nitesh.biddingPlatform.exceptions.ResourceNotFoundException;
import com.nitesh.biddingPlatform.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserDao userDao;


    public Product addProduct(JsonNode jsonNode) {
        //JsonNode -> Product object creation
        //Retrieve user obj from db
        //prod.set(user);
        //session.save(prod)

        Product product = new Product();
        product.setProductName(jsonNode.get("productName").asText());
        product.setMinimum_bid(jsonNode.get("minimum_bid").asInt());
        product.setDescription(jsonNode.get("description").asText());
        product.setActive(jsonNode.get("active").asBoolean());
        int userId = jsonNode.get("ownerId").asInt();

        int flag = 0;

        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            flag = 1;
            logger.error("Product name cannot be empty!!!");
        }
        Pattern p1 = Pattern.compile("[^A-Za-z]");
        Matcher m1 = p1.matcher(product.getProductName());
        boolean b1 = m1.find();

        if (b1) {
            flag = 1;
            logger.error("Product name cannot contain numbers or special characters!!!");
        }

        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            flag = 1;
            logger.error("Product description cannot be empty!!!");
        }

        if (product.getMinimum_bid() == 0) {
            flag = 1;
            logger.error("Minimum bid cannot be zero!!!");
        }

        if (product.getMinimum_bid() < 0) {
            flag = 1;
            logger.error("Minimum bid cannot be negative!!!");
        }

        if (userId == 0 || userId < 0) {
            flag = 1;
            logger.error("Invalid owner id!!!");
        }

        if (flag == 1)
        {
            logger.error("Product details incorrect : PRODUCT NOT ADDED!!!");
            return null;
        }

        if (flag == 0) {
            logger.info("Product details correct : PRODUCT ADDED!!!");
            System.out.println("New Product Successfully added");
        }
            return userDao.findById(userId).map(user -> {
            product.setUser(user);
            return productDao.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + userId + " not found"));
    }

    public List<Product> getProducts(){
        return productDao.findAll();
    }

    public Product getProduct(int productId){
        Optional<Product> productOptional = productDao.findById(productId);
        if(!productOptional.isPresent()){
            throw new ResourceNotFoundException("Product Id does not exist");
        }
        return productOptional.get();
    }

    public Product updateProduct(int productId, JsonNode jsonNode){
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(jsonNode.get("productName").asText());
        product.setMinimum_bid(jsonNode.get("minimum_bid").asInt());
        product.setDescription(jsonNode.get("description").asText());
        product.setActive(jsonNode.get("active").asBoolean());
        int userId = jsonNode.get("ownerId").asInt();

        int flag = 0;

        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            flag = 1;
            logger.error("Product description cannot be empty!!!");
        }

        if (product.getMinimum_bid() == 0) {
            flag = 1;
            logger.error("Minimum bid cannot be zero!!!");
        }

        if (product.getMinimum_bid() < 0) {
            flag = 1;
            logger.error("Minimum bid cannot be negative!!!");
        }

        if (flag == 1)
        {
            logger.error("Product details incorrect : PRODUCT NOT UPDATED!!!");
            return null;
        }

        if (flag == 0) {
            logger.info("Product details correct : PRODUCT UPDATED!!!");
            System.out.println("Product Successfully updated");
        }

        return userDao.findById(userId).map(user -> {
            product.setUser(user);
            return productDao.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + userId + " not found"));
    }

    public void deleteProduct(int productId){
        productDao.deleteById(productId);
    }

    public List<Product> getProductsByUserId(int userId){
        return productDao.getProductsListByBidderId(userId);
    }


}
