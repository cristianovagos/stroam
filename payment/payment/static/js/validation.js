
document.getElementsByClassName("login-form")[0].onsubmit = function(){
    var email = document.getElementsByName("email")[0];
    var password = document.getElementsByName("pass")[0];

    if(!validateEmail(email.value)) { showWarning(email); return false; }
    else if (!validatePassword(password.value)) { showWarning(password); return false; }

    return true;
};

function showWarning(input) {
    var inputDiv = input.parentElement;
    inputDiv.classList.add("alert");
};

function hideWarning(input) {
    var inputDiv = input.parentElement;
    inputDiv.classList.remove("alert");
};

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
};

function validatePassword(password){
    return password != "";
};
