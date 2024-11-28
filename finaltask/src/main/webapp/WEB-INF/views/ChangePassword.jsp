<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Change Password</title>
      <link href="<c:url value='/css/password.css' />" rel="stylesheet" />
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
        integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    </head>

    <body>
      <div class="holder">
        <div class="header">
          <div class="back-oper">
            <form action="/finaltask/users" method="get">
              <button class="back-button">
                <i class="fa-solid fa-arrow-left fa-sm"></i>
              </button>
            </form>
          </div>
          <div class="heading">Change Password</div>
        </div>

        <div class="error-back" id="errorMessage">${message}</div>
        <div class="form-container">
          <form id="changePasswordForm" action="/finaltask/users/changepassword" method="post"
            onsubmit="return validateForm()">

            <div class="form-element">
              <label for="oldPasswordField">Old Password:</label>
              <input type="password" id="oldPasswordField" placeholder="Enter your Old Password" name="oldPassword" />
            </div>
            <div class="error-msg" id="oldPasswordError"></div>
            <div class="form-element">
              <label for="newPasswordField">New Password:</label>
              <input type="password" id="newPasswordField" placeholder="Enter your new password" name="newPassword" />
            </div>
            <div class="error-msg" id="newPasswordError"></div>
            <div class="form-element">
              <label for="confirmPasswordField">Re - Enter new password:</label>
              <input type="password" id="confirmPasswordField" placeholder="Re - Enter your new password"
                name="confirmnewpassword" />
            </div>
            <div class="error-msg" id="confirmNewpasswordError"></div>
        </div>
        <div class="button-holder">
          <input type="Reset" value="Reset" class="reset-button">
          <input type="submit" value="Submit" class="submit-button">

        </div>

      </div>
      <script src="<c:url value='/javascript/password.js' />"></script>
    </body>

    </html>