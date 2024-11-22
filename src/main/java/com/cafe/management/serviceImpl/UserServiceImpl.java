package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.UserDao;
import com.cafe.management.JWT.CustomerUserDetailsService;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.JWT.JwtUtil;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.POJO.User;
import com.cafe.management.service.UserService;
import com.cafe.management.utils.CafeUtils;
import com.cafe.management.utils.EmailUtils;
import com.cafe.management.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            log.info("Inside Signup {}", requestMap);

            if (validateSignUp(requestMap)) {
                String email = requestMap.get("email");
                User user = userDao.findByEmailId(email);

                String verificationCode = generateVerificationCode();
                User newUser;

                if (user == null) {
                    newUser = getUserFromMap(requestMap, verificationCode);
                    newUser.setStatus("false");
                } else if (user.getStatus().equals("false")) {
                    newUser = user;
                    newUser.setVerificationCode(verificationCode);
                } else {
                    return CafeUtils.getResponseEntity("Email Already Exists and is Verified", HttpStatus.BAD_REQUEST);
                }

                userDao.save(newUser);
                sendVerificationCodeEmail(email, verificationCode);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Signup Successful. Verification code sent to your email.", HttpStatus.OK);
    }




    private static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }



    private boolean validateSignUp(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email")
                && requestMap.containsKey("contactNumber") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }



    private User getUserFromMap(Map<String, String> requestMap, String verificationCode) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        user.setVerificationCode(verificationCode);

        return user;


    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {

        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Verify Your Email Or Wait For Admin Approval." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception ex) {
            log.error("{}", ex);

        }
        return new ResponseEntity<String>("{\"message\":\"" + "Incorrect Email or passwword." + "\"}",
                HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {

           {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User Or Employee Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User Or Employee ID Does not exist.", HttpStatus.OK);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved.",
                    "USER:- " + user + "\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled.",
                    "USER:- " + user + "\n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);

        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);


    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)) {
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", user.getPassword());


            return CafeUtils.getResponseEntity("Check Your Mail", HttpStatus.OK);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);

            }
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new  ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> addEmployee(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateRequestMapForEmployee(requestMap)) {
                    User user = userDao.findByEmailId(requestMap.get("email"));
                    if (Objects.isNull(user)) {
                        userDao.save(getEmployeeFromMap(requestMap));
                        return CafeUtils.getResponseEntity("Employee Added Successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Employee Already Exists",HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private User getEmployeeFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setRole("Employee");
        user.setName(requestMap.get("name"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("true");
        return user;
    }

    private boolean validateRequestMapForEmployee(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email")
                && requestMap.containsKey("password") && requestMap.containsKey("contactNumber")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployee(int id) {
        try {
            if (jwtFilter.isAdmin()){
                User user = userDao.findById(id).get();
                if (Objects.isNull(user)){
                    return CafeUtils.getResponseEntity("Employee not Found on Given ID",HttpStatus.NOT_FOUND);
                } else {
                    userDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Employee Deleted Successfully",HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllEmployee() {
            try {
                if (jwtFilter.isAdmin()) {
                    List<UserWrapper> result = userDao.getAllEmployee();
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    @Override
    public ResponseEntity<String> getRole() {
        try {
            String role = jwtFilter.getRole();
            if (!Strings.isNullOrEmpty(role)) {
                return CafeUtils.getResponseEntity(role, HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Role not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateEmployeeStatus(int id, String newStatus) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(id);
                if (optional.isPresent()) {
                    User employee = optional.get();
                    employee.setStatus(newStatus);
                    userDao.save(employee);
                    return CafeUtils.getResponseEntity("Employee Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Employee not Found on Given ID", HttpStatus.NOT_FOUND);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static void sendVerificationCodeEmail(String email, String verificationCode) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("masabmughal8@gmail.com", "bbaesiaswgxouzmr");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("masabmughal8@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Verification Code");
            message.setText("Your verification code is: " + verificationCode);

            Transport.send(message);
            System.out.println("Verification code email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending verification code email.");
        }
    }

    public static void main(String[] args) {
        String email = "user@example.com";
        String verificationCode = generateVerificationCode();
        sendVerificationCodeEmail(email, verificationCode);
    }

    public ResponseEntity<String> verifyVerificationCode(Map<String, String> requestMap) {
        try {
            String email = requestMap.get("email");
            String providedVerificationCode = requestMap.get("verificationCode");

            User user = userDao.findByEmailId(email);
            if (user != null && user.getVerificationCode().equals(providedVerificationCode)) {
                user.setStatus("true");
                userDao.save(user);

                return CafeUtils.getResponseEntity("Verification code matched. Registration completed successfully.", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Invalid verification code. Registration failed.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> getUserByEmail(Map<String, String> requestMap) {
        try {
            String email = requestMap.get("email");
            User user = userDao.findByEmail(email);

            if (user != null) {
                return CafeUtils.getResponseEntity("User found: " + user.toString(), HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("User not found with the provided email.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> updateEmailContact(int id, Map<String, String> requestMap) {
        try {
            if (requestMap.containsKey("email") && requestMap.containsKey("contactNumber")) {
                String email = requestMap.get("email");
                String contactNumber = requestMap.get("contactNumber");
                Optional<User> optionalUser = userDao.findById(id);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setEmail(email);
                    user.setContactNumber(contactNumber);
                    userDao.save(user);

                    return CafeUtils.getResponseEntity("Email and Contact Number updated successfully.", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User not found with the provided ID.", HttpStatus.NOT_FOUND);
                }
            } else {
                return CafeUtils.getResponseEntity("Email and Contact Number must be provided.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}






