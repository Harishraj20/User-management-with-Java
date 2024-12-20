document.querySelector("#searchInput").addEventListener("input", searchUsers);
async function searchUsers() {

    const searchTerm = document.querySelector("#searchInput").value;

    if (!searchTerm) {
        const tbody = document.querySelector("tbody");
        tbody.innerHTML = ""; 

        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 6; 
        cell.textContent = "No users available.";
        row.appendChild(cell);
        tbody.appendChild(row);
        return;
    }


    try {
        const response = await fetch(`/finaltask/users/searchResults/${searchTerm}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const names = await response.json();
        console.log(names);  

        const tbody = document.querySelector("tbody");
        tbody.innerHTML = ""; 

        if (names.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");
            cell.colSpan = 6;  
            cell.textContent = "No users available.";  
            row.appendChild(cell);
            tbody.appendChild(row);
            return;
        }

        names.forEach(user => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.employeeId}</td>
                <td>${user.userName}</td>
                <td>${user.emailId}</td>
                <td>${user.designation}</td>
                <td>${user.role}</td>
                <td>${user.dob}</td>
            `;

            tbody.appendChild(row);
        });

    } catch (error) {
        console.error("Error fetching results:", error);
    }
}
