const newPasswordField = document.querySelector("#newPasswordField");
const confirmNewPasswordField = document.querySelector("#confirmPasswordField");
const oldPasswordField = document.querySelector("#oldPasswordField");
const formSubmitButton = document.querySelector(".submit-button");
const confirmNewPasswordError = document.querySelector("#confirmNewpasswordError");
const newPasswordError = document.querySelector("#newPasswordError");
const oldPasswordError = document.querySelector("#oldPasswordError");
const resetButton = document.querySelector(".reset-button");
const userForm = document.querySelector("#changePasswordForm");

// Display error message
function displayErrorMessage(errorElementId, field, message) {
    const errorElement = document.querySelector(`#${errorElementId}`);
    errorElement.textContent = message;
    errorElement.style.visibility = "visible";
    
    field.classList.add("invalid");
    field.classList.remove("valid");

    setTimeout(() => {
        errorElement.textContent = "";
        errorElement.style.visibility = "hidden";
        field.classList.remove("invalid");
    }, 3000);
}

// Validate a general field
function validateField(field, errorElementId) {
    if (!field.value.trim()) {
        displayErrorMessage(errorElementId, field, "This field cannot be empty.");
        return false;
    }
    field.classList.add("valid");
    field.classList.remove("invalid");
    return true;
}

// Validate a password field
function validatePasswordField(passwordField, errorElementId) {
    const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;

    if (!validateField(passwordField, errorElementId)) {
        return false;
    }

    if (!specialCharRegex.test(passwordField.value)) {
        displayErrorMessage(
            errorElementId,
            passwordField,
            "Password must contain at least one special character."
        );
        passwordField.classList.add("invalid");
        passwordField.classList.remove("valid");
        return false;
    }

    // If all validations pass, mark the field as valid
    passwordField.classList.add("valid");
    passwordField.classList.remove("invalid");
    return true;
}


// Validate old password
function validateOldPassword() {
    return validatePasswordField(oldPasswordField, "oldPasswordError");
}

// Validate new password
function validateNewPassword() {
    if (!validatePasswordField(newPasswordField, "newPasswordError")) {
        return false;
    }

    if (oldPasswordField.value === newPasswordField.value) {
        displayErrorMessage(
            "newPasswordError",
            newPasswordField,
            "New password must not be the same as the old password."
        );
        return false;
    }

    return true;
}

// Validate passwords together
function validatePasswords() {
    const isOldPasswordValid = validateOldPassword();
    const isNewPasswordValid = validateNewPassword();
    return isOldPasswordValid && isNewPasswordValid;
}

// Validate confirm password
function validateConfirmPassword() {
    if (!newPasswordField.value.trim() || !confirmNewPasswordField.value.trim()) {
        if (!newPasswordField.value.trim()) {
            displayErrorMessage("newPasswordError", newPasswordField, "Password field is empty.");
        }
        if (!confirmNewPasswordField.value.trim()) {
            displayErrorMessage("confirmNewpasswordError", confirmNewPasswordField, "Confirm password field is empty.");
        }
        return false;
    }
    if (newPasswordField.value !== confirmNewPasswordField.value) {
        displayErrorMessage("confirmNewpasswordError", confirmNewPasswordField, "Passwords do not match.");
        return false;
    }
    confirmNewPasswordField.classList.add("valid");
    confirmNewPasswordField.classList.remove("invalid");
    return true;
}

// Validate the entire form
function validateForm() {
    let isValid = true;

    isValid &= validatePasswords();
    isValid &= validateConfirmPassword();

    return Boolean(isValid);
}

// Event listeners
oldPasswordField.addEventListener("change", validateOldPassword);
newPasswordField.addEventListener("change", function () {
    validateNewPassword();
    validatePasswords();
});
confirmNewPasswordField.addEventListener("change", validateConfirmPassword);

formSubmitButton.addEventListener("click", function (event) {
    event.preventDefault();

    if (validateForm()) {
        userForm.submit();
    } else {
        console.log("Form contains errors. Please fix them before submitting.");
    }
});

resetButton.addEventListener("click", function () {
    userForm.reset();

    const errorElements = document.querySelectorAll(".error-msg");
    errorElements.forEach(element => {
        element.textContent = "";
    });

    const fields = document.querySelectorAll("input, select");
    fields.forEach(field => {
        field.classList.remove("invalid", "valid");
    });
});

const errorMessage = document.getElementById('errorMessage');
if (errorMessage) {
    setTimeout(function () {
        errorMessage.style.visibility = 'hidden';
    }, 5000)
};
