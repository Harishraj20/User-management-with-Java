const nameField = document.getElementById("name");
const passwordField = document.getElementById("userPassword");
const confirmPasswordField = document.getElementById("confirm-password");
const dobField = document.getElementById("dob-field");
const designationField = document.getElementById("designation-field");
const roleField = document.getElementById("role-field");
const emailField = document.getElementById("email-field");
const genderField = document.getElementById("gender-field");

const formSubmitButton = document.getElementById("form-submit");
const resetButton = document.querySelector(".reset-button");
const userForm = document.getElementById("userForm");

const today = new Date().toISOString().split("T")[0];
document.getElementById("dob-field").setAttribute("max", today);

function validateField(field, errorElementId) {
  const fieldValue = field.value.trim();
  const errorElement = document.getElementById(errorElementId);

  if (!fieldValue) {
    displayErrorMessage(
      errorElementId,
      field,
      `${field.name} field cannot be empty.`
    );
    return false;
  }

  errorElement.textContent = "";
  field.classList.remove("invalid");
  field.classList.add("valid");
  return true;
}

function validateEmail(emailField) {
  const emailValue = emailField.value.trim();
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailPattern.test(emailValue);
}

// function validatePassword() {
//     const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;
//     if (
//         !validateField(passwordField, "passwordError") ||
//         !specialCharRegex.test(passwordField.value)
//     ) {
//         displayErrorMessage(
//             "passwordError",
//             passwordField,
//             "Password must contain at least one special character."
//         );
//         passwordField.classList.add("invalid");
//         passwordField.classList.remove("valid");
//         return false;
//     }
//     passwordField.classList.add("valid");
//     passwordField.classList.remove("invalid");
//     return true;
// }

// function validateConfirmPassword() {
//     if (!passwordField.value.trim() || !confirmPasswordField.value.trim()) {
//         if (!passwordField.value.trim()) {
//             displayErrorMessage("passwordError", passwordField, "Password Field is Empty.");
//         }
//         if (!confirmPasswordField.value.trim()) {
//             displayErrorMessage("confirmPasswordError", confirmPasswordField, "Confirm Password Field is Empty.");
//         }
//         confirmPasswordField.classList.add("invalid");
//         confirmPasswordField.classList.remove("valid");
//         return false;
//     }
//     if (passwordField.value !== confirmPasswordField.value) {
//         displayErrorMessage("confirmPasswordError", confirmPasswordField, "Passwords do not match.");
//         confirmPasswordField.classList.add("invalid");
//         confirmPasswordField.classList.remove("valid");
//         return false;
//     }
//     confirmPasswordField.classList.add("valid");
//     confirmPasswordField.classList.remove("invalid");
//     return true;
// }

function displayErrorMessage(errorElementId, field, message) {
  const errorElement = document.getElementById(errorElementId);
  errorElement.textContent = message;
  field.classList.add("invalid");
  field.classList.remove("valid");
  setTimeout(() => {
    errorElement.textContent = "";
    field.classList.remove("invalid");
  }, 3000);
}

function validateForm() {
  let isValid = true;

  isValid &= validateField(nameField, "nameError");
  // isValid &= validatePassword();
  // isValid &= validateConfirmPassword();
  isValid &= validateField(dobField, "dobError");
  isValid &= validateField(designationField, "designationError");
  isValid &= validateField(roleField, "roleError");
  isValid &= validateField(emailField, "emailerror");
  isValid &= validateField(genderField, "genderError");

  return isValid;
}

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
  const dataError = document.querySelector(".data-error");

  dataError.textContent = "";

  const errorElements = document.querySelectorAll(".error");
  errorElements.forEach((element) => {
    element.textContent = "";
  });

  const fields = document.querySelectorAll("input, select");
  fields.forEach((field) => {
    field.classList.remove("invalid", "valid");
  });
});

nameField.addEventListener("input", function () {
  validateField(this, "nameError");
});
// passwordField.addEventListener("input", function () {
//     validatePassword();
// });
// confirmPasswordField.addEventListener("input", function () {
//     validateConfirmPassword();
// });
dobField.addEventListener("input", function () {
  validateField(this, "dobError");
});
designationField.addEventListener("input", function () {
  validateField(this, "designationError");
});
roleField.addEventListener("input", function () {
  validateField(this, "roleError");
});
emailField.addEventListener("input", function () {
  validateField(this, "emailerror");
});
genderField.addEventListener("input", function () {
  validateField(this, "genderError");
});
const errorMessage = document.getElementById("errorMessage");

if (errorMessage) {
  setTimeout(function () {
    errorMessage.style.visibility = "hidden";
  }, 5000);
}
