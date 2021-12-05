package com.nitesh.biddingPlatform.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nitesh.biddingPlatform.BiddingPlatformApplication;
import com.nitesh.biddingPlatform.model.Product;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import com.nitesh.biddingPlatform.model.User;
import com.nitesh.biddingPlatform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user) {

        int flag=0;
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty())
        {
            flag=1;
            logger.error("First name cannot be empty!!!");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty())
        {
            flag=1;
            logger.error("Second name cannot be empty!!!");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
        {
            flag=1;
            logger.error("Email cannot be empty!!!");
        }

        if (user.getContactNo() == null || user.getContactNo().trim().isEmpty())
        {
            flag=1;
            logger.error("Contact number cannot be empty!!!");
        }

        if (user.getGender() == null || user.getGender().trim().isEmpty())
        {
            flag=1;
            logger.error("Provide a valid gender!!!");
        }

        Pattern p1 = Pattern.compile("[^A-Za-z]");
        Matcher m1 = p1.matcher(user.getFirstName());
        boolean b1 = m1.find();


        Pattern p2 = Pattern.compile("[^A-Za-z]");
        Matcher m2 = p2.matcher(user.getLastName());
        boolean b2 = m2.find();

        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

        Pattern pat = Pattern.compile(emailRegex);
        boolean b3 = pat.matcher(user.getEmail()).matches();

        Pattern ptrn = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher match = ptrn.matcher(user.getContactNo());
        boolean b4 = match.find() && match.group().equals(user.getContactNo());

        if (b1 || user.getFirstName().length()>20) {
            flag=1;
            logger.error("Invalid format for first name!!!");
        }
        if (b2 || user.getFirstName().length()>20) {
            flag=1;
            logger.error("Invalid format for last name!!!");
        }
        if (b3) {
            flag=1;
            logger.error("Email address provided is not valid!!!");
        }
        if (user.getContactNo().length()!=10) {
            flag=1;
            logger.error("Contact number provided is invalid!!!");
        }

        if(flag==1)
        {
            System.out.println("\nInCorrect details entered : USER NOT SAVED");
            logger.info("User not added to sql database!!!");
        }

        if(flag==0)
        {
            System.out.println("\nCorrect details entered : USER SAVED");
            logger.info("Adding new user data to sql database!!!");
        }

        return userService.addUser(user);
    }
    @GetMapping
    public List<User> getUsers() {
        List<User> users = userService.getUsers();
        List<User> response = new ArrayList<>();
        for (User user : users) {
            try {
                user = user.shallowCopyForProducts();
                response.add(user);

            } catch (CloneNotSupportedException e) {

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        logger.info("list of users fetched from sql database!!!");

        return response;
    }

    @GetMapping(value = "{userId}")
    public User getUser(@PathVariable int userId) {
        User user = userService.getUser(userId);
        try {
            user = user.shallowCopyForProducts();
            return(user);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        logger.info("single user details fetched from sql database!!!");

        return null;
    }

    @PutMapping(value = "{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody User user){
        User updateduser = userService.updateuser(userId, user);
        try {
            updateduser = updateduser.shallowCopyForProducts();
            return(updateduser);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        logger.info("updating user details in sql database!!!");

        return null;
    }

    @RequestMapping(value = "/{userId}",method=RequestMethod.DELETE)
    public @ResponseBody void deleteUser(@PathVariable int userId){
        userService.deleteUser(userId);
        logger.info("deleting user details from sql database!!!");

    }

    @GetMapping(value = "/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s", name);
    }


}
