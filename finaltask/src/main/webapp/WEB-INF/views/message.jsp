<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!DOCTYPE html>
    <html lang="en">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Document</title>
      <link href="<c:url value='/css/message.css' />" rel="stylesheet" />

      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
        integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    </head>

    <body>
      <div class="card">
        <p class="message-content">${Message}</p>
        <p class="message-content">${message}</p>
        <p class="message-content">${msg}</p>


        <form class="button-container" action="/finaltask/users" method="get">
          <button class="back-button" type="submit">
            <i class="fa-solid fa-arrow-left fa-xs"></i> Back
          </button>
        </form>
      </div>
    </body>

    </html>