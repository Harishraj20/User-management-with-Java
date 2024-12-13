const emailField = document.getElementById("mailIdField");
const passwordField = document.getElementById("passwordField");
const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");

function validateEmail(emailField) {
  const emailError = document.getElementById("emailError");

  if (!emailField.value.trim()) {
    emailError.innerText = "Email field cannot be empty!";
    emailError.style.visibility = "visible";
    emailField.classList.add("invalid");
    return false;
  } else {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(emailField.value)) {
      emailError.innerText = "Please enter a valid email address!";
      emailError.style.visibility = "visible";
      emailField.classList.add("invalid");
      return false;
    } else {
      emailError.style.visibility = "hidden";
      emailField.classList.remove("invalid");
      emailField.classList.add("valid");
      return true;
    }
  }
}

function validateForm(event) {
  event.preventDefault();
  let isValid = true;

  if (!validateEmail(emailField)) {
    isValid = false;
    setTimeout(() => {
      emailError.style.visibility = "hidden";
      emailField.classList.remove("invalid");
    }, 8000);
  }

  if (!passwordField.value.trim()) {
    passwordError.innerText = "Password field cannot be empty!";
    console.log("Reached Here");
    console.log("1---------------------- If empty reaches here");
    passwordError.style.visibility = "visible";
    passwordField.classList.add("invalid");
    isValid = false;
    setTimeout(() => {
      passwordError.style.visibility = "hidden";
      passwordField.classList.remove("invalid");
    }, 8000);
  } else {
    passwordError.style.visibility = "hidden";
  }

  if (isValid) {
    document.getElementById("loginForm").submit();
  }
}

document.getElementById("loginForm").addEventListener("submit", validateForm);

function resetForm() {
  emailField.value = "";
  passwordField.value = "";
  emailField.classList.remove("valid", "invalid");
  passwordField.classList.remove("valid", "invalid");
  emailError.style.visibility = "hidden";
  passwordError.style.visibility = "hidden";
}

document.getElementById("loginForm").addEventListener("submit", validateForm);

emailField.onchange = () => {
  if (validateEmail(emailField)) {
    emailField.classList.add("valid");
    emailField.classList.remove("invalid");
    emailError.style.visibility = "hidden";
  } else {
    emailField.classList.add("invalid");
    emailField.classList.remove("valid");
    emailError.style.visibility = "visible";
  }
};

passwordField.onchange = () => {
  if (passwordField.value.trim()) {
    passwordField.classList.add("valid");
    passwordField.classList.remove("invalid");
    passwordError.style.visibility = "hidden";
  } else {
    passwordField.classList.add("invalid");
    passwordField.classList.remove("valid");
    passwordError.style.visibility = "visible";
  }
};
const errorMessage = document.getElementById("errorMessage");

if (errorMessage) {
  setTimeout(function () {
    errorMessage.style.visibility = "hidden";
  }, 5000);
}
