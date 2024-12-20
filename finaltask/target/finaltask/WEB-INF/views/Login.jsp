<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!DOCTYPE html>
    <html lang="en">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>User Management</title>
      <link href="<c:url value='/css/Login.css' />" rel="stylesheet" />
    </head>

    <body>
      <div class="holder">
        <div class="heading">USER MANAGEMENT</div>
        <div class="error-back" id="errorMessage">${message}</div>
        <div class="form-container">
          <form id="loginForm" action="/finaltask/login" method="post">
            <div class="form-element">
              <label for="mailIdField">EMAIL ID:</label>
              <input type="text" id="mailIdField" placeholder="Enter your email" name="emailId" autofocus />
            </div>
            <div class="error-msg" id="emailError"></div>

            <div class="form-element">
              <label for="passwordField">PASSWORD:</label>
              <input type="password" id="passwordField" placeholder="Enter your password" name="password" />
            </div>
            <div class="error-msg" id="passwordError"></div>
        </div>
        <div class="button-holder">

          <input type="Reset" value="Reset" class="reset-button" onclick="resetForm()">
          <input type="submit" value="Submit" class="submit-button">

          </form>
        </div>
      </div>
      <script src="<c:url value='/javascript/Login.js' />"></script>
    </body>

    </html>