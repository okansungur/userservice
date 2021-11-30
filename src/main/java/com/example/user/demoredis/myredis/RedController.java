package com.example.user.demoredis.myredis;


import com.example.user.demoredis.client.AsistanceRestTemplateClient;
import com.example.user.demoredis.model.User;
import com.example.user.demoredis.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/myredis", method = RequestMethod.GET, produces = "application/json")
public class RedController {

    private static final Logger logger = LogManager.getLogger(RedController.class);
    @Autowired
    AsistanceRestTemplateClient asistanceRestTemplateClient;
    @Autowired
    private UserRepository userRepository;



    @GetMapping("/addUser")
    public String addUser() {

        User caroline = new User("1", "Caroline", "Hawford", "carolinehawford@gmail.com", "123", User.Gender.FEMALE, "Bank street 128-55 ");
        User tim = new User("2", "Tim", "MayBeach", "timmay@gmail.com", "123", User.Gender.MALE, "XYZ street 133-44 ");

        userRepository.save(caroline);
        userRepository.save(tim);

        return "Users are added";
    }

    @GetMapping("/deleteUser/{userId}")
    @CacheEvict(value = "users", allEntries = true)
    public String deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        System.out.println("Deleting redis data and cache");
        return "User deleted!";
    }

    @GetMapping("/getUser")
    public String getUser() {
        List<User> user = new ArrayList<>();
        userRepository.findAll().forEach(user::add);
        return "" + user;
    }

    @Cacheable(value = "users", key = "#userId")
    @RequestMapping(value = "/getUser/{userId}", method = RequestMethod.GET)
    public User getUserById(@PathVariable String userId) {
        return userRepository.findById(userId).get();
    }

    @GetMapping("/authUser")
    public String authenticateUser() throws IOException {
        String token = "NA";
        token = getKeyCloakToken("ayseadmin", "password1");
        System.out.println(token);


        return "Token from keycloak is: \r\n" + token;

    }

    public String getKeyCloakToken(String username, String password) {
        String authServerUrl = "http://mykeycloak:8080/auth"; // Your keycloak auth entpoint
        //String authServerUrl = "http://localhost:8088/auth";
        String realm = "sng-realm";
        String clientId = "myasistance";
        Map<String, Object> clientCredentials = new LinkedHashMap<String, Object>();
        clientCredentials.put("secret", "454867dd-2737-4351-8814-53ce722bc4d7"); // Client secret (Access Type: Confidential)

        Configuration configuration = new Configuration(
                authServerUrl,
                realm,
                clientId,
                clientCredentials,
                null
        );

        AuthzClient authzClient = AuthzClient.create(configuration);
        AuthorizationRequest request = new AuthorizationRequest();
        request.setScope("offline_access");
        AuthorizationResponse response = authzClient.authorization(username, password).authorize(request);
        //response.getRefreshToken();
        return response.getToken();
    }

    @GetMapping("/authUser2")
    public String authenticateUser2() throws IOException {
        String token = "NA";
        token = getKeyCloakToken("ayseadmin", "password1");
        System.out.println(token);
        Object resp = asistanceRestTemplateClient.getAsistant(token);
        System.out.println(resp);
        return "Get Authorized data From Asistance Microservice is \r\n" + resp;

    }

}
