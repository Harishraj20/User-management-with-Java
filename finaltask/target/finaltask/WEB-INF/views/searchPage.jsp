<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Search</title>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
                integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
                crossorigin="anonymous" referrerpolicy="no-referrer" />
            <link href="<c:url value='/css/search.css' />" rel="stylesheet" />
            <style>
                #searchResults {
                    margin-top: 10px;
                }

                .search-result-item {
                    margin: 5px 0;
                    padding: 10px;
                    background-color: #f0f0f0;
                    border-radius: 5px;
                }
            </style>
        </head>

        <body>
            <p>WELCOME TO SEARCH PAGE!</p>

            <div class="searchDiv">
                <label for="searchInput">SEARCH USERS: </label>
                <input type="text" id="searchInput" placeholder="Enter User Name to Search....." />
            </div>


            <div class="table-container">
                <table>
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
                        <tr>
                            <td colspan="6">No user available</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="button-container">
                <form action="/finaltask/users" method="get">
                    <button class="back-button">
                        <i class="fa-solid fa-arrow-left fa-xs"></i> Back
                    </button>
                </form>
            </div>

        </body>
        <script src="<c:url value='/javascript/search.js' />"></script>


        </html>