package com.task.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.task.Model.User;
import com.task.Service.UserService;

@Controller
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    protected static final Logger logger = LogManager.getLogger();

    @GetMapping("/")
    public String homepage() {
        logger.info("User requesting Home page!");
        return "Login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String emailId, @RequestParam String password, HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("Attempting to authenticate user with the mail Id :{}", emailId);

        boolean isAuthenticated = service.authenticateUser(emailId, password, session);
        if (isAuthenticated) {

            logger.info("User authenticated succesfully: {}", emailId);

            logger.info("Redirecting user to /users");
            return "redirect:/users";
        } else {

            logger.warn("Authentication failed for user with Email Id:{}", emailId);
            logger.info("Redirecting user to /");

            redirectAttributes.addFlashAttribute("message", "Invalid Email-Id or password!");
            return "redirect:/";
        }
    }

    @GetMapping("/users")
    public String showUserPage(@RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize, HttpSession session, Model model) {

        service.prepareUserPage(pageNumber, pageSize, session, model);

        logger.info("Redirecting User to Home page succesfully!");

        return "Details";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        logger.info("Invalidating user!!");
        session.invalidate();

        logger.info("Invalidated session on user logout and redirecting to Login page");

        return "redirect:/";
    }

    @GetMapping("/users/viewInfo/{userId}")
    public String viewInfos(@PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model,
            HttpSession session) {

        logger.info("Request to view info for user with ID: {} (Page: {}, Page Size: {})", userId, page, pageSize);

        service.prepareLoginInfoPage(userId, page, pageSize, model);

        logger.info("Returning LoginInfo page for user with ID: {}", userId);
        return "LoginInfo";
    }

    @GetMapping("/users/inactiveUsers")
    public String viewInactiveUsers(Model model, HttpSession session,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        logger.info("request to view inactive users!");

        logger.info("Preparing inactive user info page");

        service.prepareInactiveUsersPage(pageNumber, pageSize, session, model);

        logger.info("Displaying inactiveusers list to the user!");

        return "InactiveUsers";
    }

    @GetMapping("/users/changepassword")
    public String changePasswordPage(HttpSession session, Model model) {

        logger.info("User requesting for change password page");

        return "ChangePassword";
    }

    @PostMapping("/users/changepassword")
    public String changePassword(HttpSession session, @RequestParam String oldPassword,
            @RequestParam String newPassword, Model model) {

        logger.info("Received password change request for user with session ID: {}", session.getId());

        boolean isPasswordChanged = service.updateUserPassword(session, oldPassword, newPassword);

        if (isPasswordChanged) {
            logger.info("Password updated successfully for user with session ID: {}", session.getId());
            model.addAttribute("Message", "Password Updated Successfully");

            session.invalidate();
            return "Redirect";

        } else {

            logger.warn("Failed to update password for user with session ID: {}", session.getId());
            model.addAttribute("message", "Incorrect old password.");
            return "ChangePassword";

        }
    }
    @GetMapping("users/search")
    public String viewSearchPage() {
        return "search";
    }
    
    @GetMapping("users/searchResults/{query}")
    @ResponseBody
    public List<User> searchResults(@PathVariable String query) {
        logger.info("Request to search user by name");
        return service.getBySearch(query);

    }

}
