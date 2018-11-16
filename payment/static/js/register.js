document.getElementsByClassName("register-form")[0].onsubmit = function(){
    // Getting the inputs to validate
    var name = document.getElementsByName("name")[0];
    var email = document.getElementsByName("email")[0];
    var password = document.getElementsByName("pass")[0];
    var re_password = document.getElementsByName("re-pass")[0];

    // Hidding eventual old warnings
    hideWarning(email);
    hideWarning(password);
    hideWarning(name);
    hideWarning(re_password);

    // Validation
    if(!validateContent(name.value)) { showWarning(name); return false; }
    else if(!validateEmail(email.value)) { showWarning(email); return false; }
    else if (!validateContent(password.value)) { showWarning(password); return false; }
    else if (!validateContent(re_password.value))  { showWarning(re_password); return false; }
    else if (password.value != re_password.value ) {showWarning(password); showWarning(re_password); return false;}

    // Nothing went wrong
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

function validateContent(content){
    return content != "";
};
