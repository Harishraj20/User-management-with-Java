package com.task.Service;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.task.Model.Login;
import com.task.Model.User;
import com.task.Model.UserPrincipal;
import com.task.Repository.UserRepository;
import com.task.Security.JwtService;

@Service
public class UserService {

    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repo = repo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger logger = LogManager.getLogger();
    String token;

    //Authentication of User
    public boolean authenticateUser(String emailId, String password, HttpSession session, HttpServletResponse response) {
        logger.info("Authenticate reaches service method!");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailId, password)
            );

            if (authentication.isAuthenticated()) {
                logger.info("User authenticated successfully!");
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                User user = userPrincipal.getUser(); //Returns the user object

                System.out.println("User details: " + user);

                session.setAttribute("LoginUser", user);
                updateCredentials(user);
                token = jwtService.generateToken(emailId);

                // response.setHeader("access_token", token);
                // logger.info("Response header 'access_token' set with value: " + response.getHeader("access_token"));

                logger.info("Token Generated on successful Login: {}", token);

                Cookie jwtCookie = new Cookie("jwtToken", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/finaltask");
                response.addCookie(jwtCookie);

                return true;
            } else {
                logger.info("Authentication failed!");
                return false;
            }

        } catch (BadCredentialsException e) {
            logger.error("Bad credentials error: Invalid email or password!");
            return false;
        } catch (Exception e) {
            logger.error("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    //UserPage with pagnation
    public void prepareUserPage(int pageNumber, int pageSize, Model model) {

        logger.info("Preparing user page for page number: {} with page size: {}", pageNumber, pageSize);

        int offset = (pageNumber - 1) * pageSize;

        List<User> paginatedUsers = repo.fetchUsersWithPagination(offset, pageSize);
        String mailId = jwtService.extractUsername(token);
        logger.info("The extracted email id from the jwt token is: {} ", mailId);

        int totalUsers = repo.countTotalUsers();

        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        model.addAttribute("UserList", paginatedUsers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("loggedInUser", repo.checkUserByEmailid(mailId));
        logger.info("Retrieved user info from token mail Id: {}", model.getAttribute("loggedInUser"));

        logger.info("User page prepared with total users: {} and total pages: {}", totalUsers, totalPages);
    }

    //Add Users
    public boolean addUsers(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Attempting to add user with emailId: {}", user.getEmailId());

        if (repo.checkUserByEmailid(user.getEmailId()) != null) {
            logger.warn("User with emailId: {} already exists.", user.getEmailId());
            return false;
        }

        boolean isAdded = repo.addUserInfo(user);

        logger.info("User with emailId: {} added successfully.", user.getEmailId());

        return isAdded;
    }

    //Updating credentials on Login
    public void updateCredentials(User user) {
        logger.info("Updating credentials for user with emailId: {}", user.getEmailId());

        user.setLoginStatus(user.getLoginStatus() + 1);
        repo.updateUser(user);

        Login userLog = new Login();
        userLog.setUser(user);
        userLog.setLoginInfo(LocalDateTime.now());
        repo.saveLoginInfo(userLog);

        logger.info("Credentials updated for user with emailId: {}", user.getEmailId());
    }

    //Fetching Inactive Users 
    public List<User> fetchInactiveUsers(int pageNumber, int pageSize) {
        logger.info("Fetching inactive users (Page: {}, Size: {})", pageNumber, pageSize);
        int offset = (pageNumber - 1) * pageSize;
        return repo.findInactiveUsers(offset, pageSize);
    }

    //Delete User
    public boolean deleteUserById(int userId) {
        logger.info("Attempting to delete user with ID: {}", userId);
        User user = repo.findUser(userId);
        if (user != null) {
            repo.deleteUser(userId);
            logger.info("User with ID: {} deleted successfully", userId);
            return true;
        } else {
            logger.warn("User with ID: {} not found for deletion", userId);
            return false;
        }
    }

    //Locate user by ID
    public User findUserById(int userIdForAction) {
        logger.info("Fetching user with ID: {}", userIdForAction);
        return repo.findUser(userIdForAction);
    }

    //Update User Details
    public boolean updateUsers(User updateUser, int paramId) {
        logger.info("Attempting to update user with ID: {}", paramId);

        User existingUserWithEmail = repo.findUserByEmailExcludingId(updateUser.getEmailId(), paramId);
        if (existingUserWithEmail != null) {
            logger.warn("Email ID: {} is already in use by another user.", updateUser.getEmailId());
            return false;
        }

        User existingUser = repo.findUser(paramId);
        if (existingUser != null) {
            existingUser.setUserName(updateUser.getUserName());
            existingUser.setDesignation(updateUser.getDesignation());
            existingUser.setEmailId(updateUser.getEmailId());
            existingUser.setDob(updateUser.getDob());
            existingUser.setGender(updateUser.getGender());

            repo.updateUser(existingUser);
            logger.info("User with ID: {} updated successfully", paramId);
            return true;
        } else {
            logger.warn("User with ID: {} not found for update", paramId);
            return false;
        }
    }

    //Pagination for Login Details
    public void prepareLoginInfoPage(String userId, int page, int pageSize, Model model) {

        logger.info("Preparing login info page for userId: {} (Page: {}, Size: {})", userId, page, pageSize);

        int user_id = Integer.parseInt(userId);

        List<Login> logins = repo.getLoginInfo(user_id, page, pageSize);

        int totalLogins = repo.getTotalLoginCount(user_id);

        int totalPages = (int) Math.ceil((double) totalLogins / pageSize);

        model.addAttribute("userId", user_id);
        model.addAttribute("Loggedinfo", logins);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalLogins", totalLogins);

        logger.info("Login info page prepared for userId: {} with total logins: {} and total pages: {}", user_id,
                totalLogins, totalPages);
    }

    //Pagination for Inactive users page
    public void prepareInactiveUsersPage(int pageNumber, int pageSize, HttpSession session, Model model) {
        logger.info("Preparing inactive users page for page number: {} with page size: {}", pageNumber, pageSize);

        User loginUser = (User) session.getAttribute("LoginUser");

        int offset = (pageNumber - 1) * pageSize;

        List<User> paginatedInactiveUsers = repo.findInactiveUsers(offset, pageSize);

        int totalInactiveUsers = repo.countInactiveUsers();

        int totalPages = (int) Math.ceil((double) totalInactiveUsers / pageSize);

        model.addAttribute("UserList", paginatedInactiveUsers);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("inactiveUserCount", totalInactiveUsers);
        model.addAttribute("empId", loginUser.getEmployeeId());

        logger.info("Inactive users page prepared with total inactive users: {} and total pages: {}",
                totalInactiveUsers, totalPages);
    }

    //Update User passowrd Functionality
    public boolean updateUserPassword(HttpSession session, String oldPassword, String newPassword) {

        logger.info("Attempting to update password for user with emailId: {}",
                ((User) session.getAttribute("LoginUser")).getEmailId());

        User loginUser = (User) session.getAttribute("LoginUser");

        User user = repo.checkUserByEmailid(loginUser.getEmailId());

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            repo.updateUser(user);
            logger.info("Password updated successfully for user with emailId: {}", loginUser.getEmailId());
            return true;
        } else {
            logger.warn("Old password mismatch for user with emailId: {}", loginUser.getEmailId());
            return false;
        }
    }

    public List<User> getBySearch(String field) {
        logger.info("Requesting service to interact with database");
        return repo.searchResults(field);
    }
}
