<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Inactive Users</title>
      <link href="<c:url value='/css/inactiveusers.css' />" rel="stylesheet" />
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
        integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    </head>

    <body>
      <div class="user-details">
        <div class="display-user">
          <p>USER ID : ${empId}</p>
        </div>
        <div class="display-title">
          <p>INACTIVE USERS</p>
        </div>
        <div class="logout-field">
          <P>USER COUNTS : ${inactiveUserCount}</P>
        </div>
      </div>


      <div class="table-container">
        <table border="1">
          <thead>
            <tr>
              <th style="width: 10%">User Id</th>
              <th style="width: 20%">Name</th>
              <th>Email Id</th>
              <th>Designation</th>
              <th>Role</th>
              <th>Date of Birth</th>
            </tr>
          </thead>
          <tbody>
            <c:if test="${empty UserList}">
              <tr>
                <td colspan="6" style="text-align: center">No Inactive Users!</td>
              </tr>
            </c:if>
            <c:if test="${not empty UserList}">
              <c:forEach var="user" items="${UserList}">
                <tr>
                  <td>${user.employeeId}</td>
                  <td>${user.userName}</td>
                  <td>${user.emailId}</td>
                  <td>${user.designation}</td>
                  <td>${user.role}</td>
                  <td>${user.dob}</td>
                </tr>
              </c:forEach>
            </c:if>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="pagination">
        <c:if test="${currentPage > 1}">
          <a href="/finaltask/users/inactiveUsers?pageNumber=${currentPage - 1}&pageSize=${pageSize}"
            class="page-link">Previous</a>
        </c:if>

        <c:forEach begin="1" end="${totalPages}" var="i">
          <c:choose>
            <c:when test="${i == currentPage}">
              <span class="current-page">${i}</span>
            </c:when>
            <c:otherwise>
              <a href="/finaltask/users/inactiveUsers?pageNumber=${i}&pageSize=${pageSize}" class="page-link">${i}</a>
            </c:otherwise>
          </c:choose>
        </c:forEach>


        <c:if test="${currentPage < totalPages}">
          <a href="/finaltask/users/inactiveUsers?pageNumber=${currentPage + 1}&pageSize=${pageSize}"
            class="page-link">Next</a>
        </c:if>
      </div>

      <div class="button-container">
        <form action="/finaltask/users" method="get">
          <button class="back-button">
            <i class="fa-solid fa-arrow-left fa-xs"></i> Back
          </button>
        </form>
      </div>
    </body>

    </html>