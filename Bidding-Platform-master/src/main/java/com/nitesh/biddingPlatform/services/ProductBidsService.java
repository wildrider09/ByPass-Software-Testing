package com.nitesh.biddingPlatform.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.nitesh.biddingPlatform.api.UserResource;
import com.nitesh.biddingPlatform.dao.ProductBidsDao;
import com.nitesh.biddingPlatform.dao.ProductDao;
import com.nitesh.biddingPlatform.dao.UserDao;
import com.nitesh.biddingPlatform.exceptions.InvalidEntryException;
import com.nitesh.biddingPlatform.exceptions.ResourceNotFoundException;
import com.nitesh.biddingPlatform.model.Product;
import com.nitesh.biddingPlatform.model.ProductBids;
import com.nitesh.biddingPlatform.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProductBidsService {
    private static final Logger logger = LoggerFactory.getLogger(ProductBidsService.class);

    @Autowired
    private ProductBidsDao bidDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserDao userDao;

    public ProductBids addBid(JsonNode jsonNode){
        ProductBids bid = new ProductBids();
        bid.setBidAmount(jsonNode.get("bidAmount").asInt());
        bid.setSelected(jsonNode.get("selected").asBoolean());
        //to get the product object
        int productId = jsonNode.get("bidProductId").asInt();
        Optional<Product> optionalProduct = productDao.findById(productId);
        if(!optionalProduct.isPresent()){
            throw new ResourceNotFoundException("Product with such product ID does not exist");
        }
        Product prod = optionalProduct.get();
        //to get the user object
        int userId = jsonNode.get("bidOwnerId").asInt();
        Optional<User> optionalUser = userDao.findById(userId);
        if(!optionalUser.isPresent()){
            logger.error("Invalid user id!!!");
            throw new ResourceNotFoundException("User with such user ID does not exist");
        }
        User bidder = optionalUser.get();
        if(prod.getUser().getId() == userId){
            logger.error("Cannot bid on it's own product!!!");
            throw new InvalidEntryException("Can not bid on it's own product");
        }

        if(bid.getBidAmount()<=0) {
            logger.error("Cannot bid with zero or negative amount!!!");
            throw new InvalidEntryException("Cannot bid with zero or negative amount");
        }

        if(prod.getMinimum_bid() > bid.getBidAmount()){
            logger.error("Cannot bid with amount less tha minimum bid!!!");
            throw new InvalidEntryException("Can not bid with amount less than minimum bid");
        }

        if(prod.getUser()==null) {
            logger.error("Bid owner not selected!!!");
        }

        bid.setProductToBid(prod);
        bid.setBidOwner(bidder);
        return bidDao.save(bid);
    }

    public List<ProductBids> getBids(){
        return bidDao.findAll();
    }

    public ProductBids getBid(int bidId){
        Optional<ProductBids> optionalProductBid = bidDao
                .findById(bidId);
        if(!optionalProductBid.isPresent()){
            throw new ResourceNotFoundException("Bid Id does not exist");
        }
        return optionalProductBid.get();
    }

    public ProductBids updateBid(int bidId, JsonNode jsonNode){
        ProductBids bid = new ProductBids();
        bid.setBidId(bidId);
        bid.setBidAmount(jsonNode.get("bidAmount").asInt());
        bid.setSelected(jsonNode.get("selected").asBoolean());
        //to get the product object
        int productId = jsonNode.get("bidProductId").asInt();
        Optional<Product> optionalProduct = productDao.findById(productId);
        if(!optionalProduct.isPresent()){
            throw new ResourceNotFoundException("Product with such product ID does not exist");
        }
        Product prod = optionalProduct.get();
        //to get the user object
        int userId = jsonNode.get("bidOwnerId").asInt();
        Optional<User> optionalUser = userDao.findById(userId);
        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User with such user ID does not exist");
        }
        User bidder = optionalUser.get();
        if(prod.getUser().getId() == userId){
            throw new InvalidEntryException("Cannot bid on it's own product");
        }
        if(prod.getMinimum_bid() > bid.getBidAmount()){
            throw new InvalidEntryException("Cannot place bid with amount less than minimum bid");
        }
        logger.info("Input details correct : PRODUCT BID PLACED!!!");
        System.out.println("Product bid placed successfully");

        bid.setProductToBid(prod);
        bid.setBidOwner(bidder);
        return bidDao.save(bid);
    }

    public void deleteBid(int bidId){
        try{
            bidDao.deleteById(bidId);
        }
        catch (ResourceNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public List<ProductBids> getBidsByProductId(int productId)
    {
        return bidDao.getBidsByProductId(productId);
    }


}
