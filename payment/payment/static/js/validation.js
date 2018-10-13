
if(document.getElementsByClassName("login-form")[0]){
  document.getElementsByClassName("login-form")[0].onsubmit = function(){
      var email = document.getElementsByName("email")[0];
      var password = document.getElementsByName("pass")[0];

      if(!validateEmail(email.value)) { showWarning(email); return false; }
      else if (!validatePassword(password.value)) { showWarning(password); return false; }

      return true;
  };
}
else {
  document.getElementsByClassName("payment-form")[0].onsubmit = function(){
      document.getElementById("loading").style.display = "block";
      document.getElementById("proccess").innerHTML = "Processing, please wait"
      document.getElementById("proccess").disabled = true;

      var card_number = document.getElementsByName("card-number")[0];
      var expiration = document.getElementsByName("exp")[0];
      var cvc = document.getElementsByName("cvc")[0];
      var card_owner = document.getElementsByName("card-owner")[0];

      // Validation goes here TODO
      return false;
  };
}

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
