package com.csdg1t3.ryverbankapi.user;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import javax.validation.Valid;


/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests by calling methods in userService
 */
@RestController
public class UserController {
    private UserService userService;
    private BCryptPasswordEncoder encoder;


    public UserController(UserService us, BCryptPasswordEncoder encoder) {
        this.userService = us;
        this.encoder = encoder;
    }

    // get all methods 

    /**
     * List all customers in the system
     * @return list of all books
     */
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userService.listUsers("ROLE_USER");
    }

    @GetMapping("/analysts")
    public List<User> getAnalysts() {
        return userService.listUsers("ROLE_ANALYST");
    }

    // might not need 
    @GetMapping("/managers")
    public List<User> getManagers() {
        return userService.listUsers("ROLE_MANAGER");
    }

    /**
     * Search for customer with the given id
     * If there is no customer with the given "id", throw a UserNotFoundException
     * @param id
     * @return customer with the given id
     */
    @PostAuthorize("#id == authentication.principal.id or hasRole('ROLE_MANAGER')")
     @GetMapping("/customers/{id}")
    public User getCustomer(@PathVariable Long id) {
        User customer = userService.getUser(id);

        // Need to handle "customer not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if(customer == null || !customer.getStringAuthorities().contains("ROLE_USER")) throw new UserNotFoundException(id);
        return userService.getUser(id);
    }

   // add methods 
   /**
    * General add user method, check if authorities is matching
    * @param user
    * @param authority
    * @return
    */
   public User addUser(User user, String authority) {
    // check if authority of user matches where it is supposed to be mapped to 
    String authorities = user.getStringAuthorities();
    if(!authorities.contains(authority)){
        // throw exception 
        throw new UserNotValidException("authorities of" + authority + " is wrong" );
    }
    
    // encrpyt customer's password 
    user.setPassword(encoder.encode(user.getPassword()));
    return userService.addUser(user);
}

    /**
     * Add a new customer with POST request to "/customers"
     * Note the use of @RequestBody
     * @param user
     * @return list of all customers 
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public User addCustomer(@Valid @RequestBody User customer) {
        return addUser(customer,"ROLE_USER");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/analysts")
    public User addAnalyst(@Valid @RequestBody User analyst) {
        return addUser(analyst,"ROLE_ANALYSTS");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/managers")
    public User addManager(@Valid @RequestBody User manager) {
        return addUser(manager,"ROLE_MANAGER");
    }


    // update methods 
    /**
     * General update method for manager/ analyst/ customer 
     * @param id
     * @param newUser
     * @param authority
     * @return
     */
    public User updateUser(Long id, User newUser, String authority) {
    return userService.updateUser(id,newUser, authority); 
    }

    /**
     * If there is no customer with the given "id", UserNotFoundException
     * @param id
     * @param newCustomer
     * @return the updated, or newly added customer
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_MANAGER')")
    //@PutMapping("/customers/{id}")
    @RequestMapping(value = "/customers/{id}" , method = RequestMethod.PUT, headers = "updateCustomerInfo")
    public User updateCustomerInfo(@PathVariable Long id, @Valid @RequestBody User newCustomer) {
        return updateUser(id,newCustomer,"ROLE_USER");
    }

    // @PreAuthorize("hasRole('ROLE_MANAGER') ")
    // // @PutMapping("/customers/{id}")
    // @RequestMapping(value = "/customers/{id}" ,method = RequestMethod.PUT, headers = "updateCustomerStatus")
    // public User updateCustomerStatus(@PathVariable Long id, @Valid @RequestBody User newCustomer) {
    //     return updateUser(id,newCustomer,"ROLE_MANAGER");
    // }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_MANAGER') ")
    @PutMapping("/analyst/{id}")
    public User updateAnalyst(@PathVariable Long id, @Valid @RequestBody User newAnalyst) {
        return updateUser(id,newAnalyst,"ROLE_ANALYST");
    }


    /**
     * Remove a customer with the DELETE request to "/customers/{id}"
     * If there is no customer with the given "id", UserNotFoundException
     * @param id
     */
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        deleteUser(id, "ROLE_USER");
    }

    @DeleteMapping("analyst/{id}")
    public void deleteAnalyst(@PathVariable Long id) {
        deleteUser(id, "ROLE_Analyst");
    }

    public void deleteUser(Long id, String authority){
        User userAtId = userService.getUser(id);
        if (userAtId == null ||!userAtId.getStringAuthorities().contains(authority)){
            throw new UserNotFoundException(id);
        }

        if(userAtId.getAuthorities().size() == 1 ){
            try {
                userService.deleteUser(id);
            } catch (Exception e) {
                throw new UserNotFoundException(id);
            }
            return;
        }

        // user has 2 roles 
        String newAuthorities = "";
        String[] authorities = userAtId.getAuthorities().toArray(new String[0]);
        // String[] authorities = userAtId.getAuthorities();
        for(String role : authorities){
            if(!role.equals(authority)){
                newAuthorities += role;
            }
        }
        userAtId.setAuthorities(newAuthorities);
        userService.updateUser(id, userAtId,"ROLE_MANAGER");
    }

}