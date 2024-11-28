<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!DOCTYPE html>
    <html lang="en">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>success</title>
      <link href="<c:url value='/css/redirect.css' />" rel="stylesheet" />

      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
        integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    </head>
    <div class="card">
      <p class="message-content" style="font-size: larger; height: 20%; margin-top: 0px;">${Message}</p>
      <p class="description-content" style="font-size: large; height: 40%;">
        Kindly Log in again with your new password.
        You will be automatically redirected to the Home page in 10 seconds....
      </p>
      <p class="redirect-message" style="margin-top: 2%; font-size: large; height:30%;">If you are not redirected automatically,
        please <a href="/finaltask">click here</a> to go to the Home page.</p>
    </div>

    <script type="text/javascript">
      setTimeout(function () {
        window.location.href = "/finaltask";
      }, 10000); 
    </script>
    </body>

    </html>