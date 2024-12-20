const closeButtonForLogin = document.querySelector("#close-button-for-login");
const closeButtonForAdd = document.querySelector("#close-button-for-add");
const addUserModal = document.querySelector("#add-user-modal");
const createUserButton = document.querySelector("#create-button");
const loginUserButton = document.querySelector("#login-button");
const loginUserModal = document.querySelector("#login-user-modal");
const viewDetailsButton = document.querySelector("#view-button");
const username = document.querySelector("#name");
const userPassword = document.querySelector("#userPassword");
const confirmPassword = document.querySelector("#confirm-password");
const nameError = document.querySelector("#nameError");
const passwordError = document.querySelector("#passwordError");
const resetButton = document.querySelector(".reset-button");
const loginName = document.querySelector("#loginname");
const loginPassword = document.querySelector("#loginPassword");
const loginNameError = document.querySelector("#loginNameError");
const loginPasswordError = document.querySelector("#loginPasswordError");
const confirmPasswordError = document.querySelector("#confirmPasswordError");
const errorMessage = document.querySelector(".error-message");

createUserButton.addEventListener("click", () => {
  addUserModal.style.display = "flex";
  username.focus();
});

loginUserButton.addEventListener("click", () => {
  loginUserModal.style.display = "flex";
  loginName.focus();
});

closeButtonForAdd.addEventListener("click", () => {
  addUserModal.style.display = "none";
});

closeButtonForLogin.addEventListener("click", () => {
  loginUserModal.style.display = "none";
});

viewDetailsButton.addEventListener("click", () => {
  window.location.href = "/finaltask/Views";
});

username.onchange = () =>
  validateField(username, nameError, "User name Field is Empty.");
userPassword.onchange = () => validatePassword();
confirmPassword.oninput = () => validateConfirmPassword();

function validateField(field, errorElement, emptyMessage) {
  if (!field.value.trim()) {
    displayErrorMessage(errorElement, field, emptyMessage);
    return false;
  }
  errorElement.innerText = "";
  field.classList.add("valid");
  field.classList.remove("invalid");
  return true;
}

function validatePassword() {
  const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;
  if (
    !validateField(userPassword, passwordError, "Password Field is Empty.") ||
    !specialCharRegex.test(userPassword.value)
  ) {
    displayErrorMessage(
      passwordError,
      userPassword,
      "Password must contain at least one special character."
    );
    console.log("Im here");
    return false;
  }
  passwordError.innerText = "";
  userPassword.classList.add("valid");
  confirmPassword.classList.add("valid");
  userPassword.classList.remove("invalid");
  return true;
}
function validateConfirmPassword() {
  if (!userPassword.value.trim() || !confirmPassword.value.trim()) {

    if (!userPassword.value.trim()) {
      displayErrorMessage(
        passwordError,
        userPassword,
        "Password Field is Empty."
      );

    }
    if (!confirmPassword.value.trim()) {
      displayErrorMessage(
        confirmPasswordError,
        confirmPassword,
        "Confirm Password Field is Empty."
      );
    }
    return false;
  }
  if (userPassword.value !== confirmPassword.value) {
    displayErrorMessage(
      passwordError,
      confirmPassword,
      "Passwords Does Not Match."
    );
    return false;
  }
  confirmPasswordError.innerText = "";
  confirmPassword.classList.add("valid");
  confirmPassword.classList.remove("invalid");
  return true;
}

function validateFields() {
  const isUsernameValid = validateField(
    username,
    nameError,
    "User name Field is Empty."
  );
  const isPasswordValid = validatePassword();
  const isConfirmPasswordValid = validateConfirmPassword();

  if (!isUsernameValid) username.focus();
  else if (!isPasswordValid) userPassword.focus();
  else if (!isConfirmPasswordValid) confirmPassword.focus();

  return isUsernameValid && isPasswordValid && isConfirmPasswordValid;
}
loginName.onchange = () =>
  validateField(loginName, loginNameError, "User name Field is empty.");
loginPassword.onchange = () =>
  validateField(loginPassword, loginPasswordError, "Password Field is Empty.");

document.querySelector("#loginUserForm").onsubmit = validateLoginFields;

function validateLoginFields() {
  const isUsernameValid = validateField(
    loginName,
    loginNameError,
    "User name Field is empty."
  );
  const isPasswordValid = validateField(
    loginPassword,
    loginPasswordError,
    "Password Field is Empty."
  );
  if (!isUsernameValid) loginName.focus();
  else if (!isPasswordValid) loginPassword.focus();
  return isUsernameValid && isPasswordValid;
}
function displayErrorMessage(selector, inputField, message) {
  selector.innerText = message;
  selector.style.visibility = "visible";
  inputField.classList.add("invalid");
  inputField.classList.remove("valid");
  inputField.focus();

  setTimeout(() => {
    selector.innerText = "";
    selector.style.visibility = "hidden";
    inputField.classList.remove("invalid");
  }, 8000);
}
function resetValidation(fields, errorElements) {
  errorElements.forEach(error => {
    error.innerText = "";
  });

  fields.forEach(field => {
    field.classList.remove("valid", "invalid");
  });
  fields[0].focus();
}

resetButton.onclick = () => {
  resetValidation(
    [username, userPassword, confirmPassword],
    [nameError, passwordError, confirmPasswordError]
  );
};
document.querySelector("#login-reset-button").onclick = () => {
  resetValidation(
    [loginName, loginPassword],
    [loginNameError, loginPasswordError]
  );
};

