<%@ page contentType="text/html;charset=UTF-8" language="java"
isELIgnored="false" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Users Details</title>
    <link href="<c:url value='/css/table.css' />" rel="stylesheet" />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
      integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
      crossorigin="anonymous"
      referrerpolicy="no-referrer"
    />
  </head>
  <body>
    <c:set var="loggedInUser" value="${sessionScope.LoginUser}" />

    <div id="toast" class="toast"></div>

    <div class="user-details">
      <div class="display-user">
        <p>USER ID: ${loggedInUser.employeeId}</p>
      </div>
      <div class="display-title">
        <p>MANAGE USERS</p>
      </div>

      <div class="logout-field">
        <form action="/finaltask/logout" method="get">
          <input type="hidden" name="viewUser" value="${loggedInUser.userId}" />
          <button class="logout-btn">Log-out</button>
        </form>
      </div>
    </div>

    <div
      class="addUser-btn ${loggedInUser.role eq 'admin' ? 'admin' : 'viewer'}"
    >
      <form action="/finaltask/users/inactiveUsers" method="get">
        <button class="add-btn">Inactive Users</button>
      </form>
      <form action="/finaltask/users/search" method="get">
        <button class="add-btn">Search Users</button>
      </form>
      <c:if test="${loggedInUser.role eq 'admin'}">
        <form action="/finaltask/users/addform" method="get">
          <button class="add-btn">Add User</button>
        </form>
      </c:if>
      <form action="/finaltask/users/changepassword" method="get">
        <button class="add-btn">Change password</button>
      </form>
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
            <th style="width: 20%">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>${loggedInUser.employeeId}</td>
            <td>${loggedInUser.userName}</td>
            <td>${loggedInUser.emailId}</td>
            <td>${loggedInUser.designation}</td>
            <td>${loggedInUser.role}</td>
            <td>${loggedInUser.dob}</td>
            <td>
              <div class="action-holder">
                <form
                  action="/finaltask/users/viewInfo/${loggedInUser.userId}"
                  method="get"
                >
                  <button class="btn view-btn">View</button>
                </form>
                <form
                  action="/finaltask/users/updateform/${loggedInUser.userId}"
                  method="get"
                >
                  <button class="btn edit-btn">Edit</button>
                </form>
              </div>
            </td>
          </tr>

          <c:forEach var="user" items="${UserList}">
            <c:if test="${user.userId != loggedInUser.userId}">
              <tr>
                <td>${user.employeeId}</td>
                <td>${user.userName}</td>
                <td>${user.emailId}</td>
                <td>${user.designation}</td>
                <td>${user.role}</td>
                <td>${user.dob}</td>
                <td>
                  <div class="action-holder">
                    <form
                      action="/finaltask/users/viewInfo/${user.userId}"
                      method="get"
                    >
                      <button class="btn view-btn">View</button>
                    </form>
                    <c:choose>
                      <c:when test="${loggedInUser.role eq 'admin'}">
                        <form
                          action="/finaltask/users/delete/${user.userId}"
                          method="post"
                          onsubmit="return confirm('Are you sure you want to delete the employee with id: ${user.employeeId}?');"
                        >
                          <button class="btn delete-btn">Delete</button>
                        </form>
                      </c:when>
                    </c:choose>
                  </div>
                </td>
              </tr>
            </c:if>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <c:if test="${currentPage > 1}">
        <a href="/finaltask/users?pageNumber=${currentPage - 1}&pageSize=10"
          >Previous</a
        >
      </c:if>

      <c:forEach begin="1" end="${totalPages}" var="i">
        <c:choose>
          <c:when test="${i == currentPage}">
            <span class="current-page">${i}</span>
          </c:when>
          <c:otherwise>
            <a href="/finaltask/users?pageNumber=${i}&pageSize=10">${i}</a>
          </c:otherwise>
        </c:choose>
      </c:forEach>

      <c:if test="${currentPage < totalPages}">
        <a href="/finaltask/users?pageNumber=${currentPage + 1}&pageSize=10"
          >Next</a
        >
      </c:if>
    </div>

    <script>
      const toast = document.getElementById("toast");
      const toastMessage = "${toastMessage}"; 
      if (toastMessage) {
        toast.textContent = toastMessage;
        toast.classList.add("visible");
        setTimeout(() => {
          toast.classList.remove("visible");
        }, 5000);
      }
    </script>
    
  </body>
</html>
