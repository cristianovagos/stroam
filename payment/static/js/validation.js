
// If user is not logged in the validation will be on the login form
if(document.getElementsByClassName("login-form")[0]){
  document.getElementsByClassName("login-form")[0].onsubmit = function(){
      // Getting the inputs to validate
      var email = document.getElementsByName("email")[0];
      var password = document.getElementsByName("pass")[0];

      // Hidding eventual old warnings
      hideWarning(email);
      hideWarning(password);

      // Validation
      if(!validateEmail(email.value)) { showWarning(email); return false; }
      else if (!validateContent(password.value)) { showWarning(password); return false; }

      // Nothing went wrong
      return true;
  };
}
// If user is logged in the validation will be on the payment details
else {
  document.getElementsByClassName("payment-form")[0].onsubmit = function(){
      // Getting the inputs to validate
      var card_number = document.getElementsByName("card-number")[0];
      var expiration = document.getElementsByName("exp")[0];
      var cvc = document.getElementsByName("cvc")[0];
      var card_owner = document.getElementsByName("card-owner")[0];

      // Hidding eventual old warnings
      hideWarning(card_number);
      hideWarning(expiration);
      hideWarning(cvc);
      hideWarning(card_owner);

      // Validation
      var valid = true;
      if (!validateContent(card_number.value) || !valid_credit_card(card_number.value)) { showWarning(card_number); valid = false }
      if (!validateExpDate(expiration.value)) { showWarning(expiration); valid = false }
      if (!validateContent(cvc.value)) { showWarning(cvc); valid = false }
      if (!validateContent(card_owner.value)) { showWarning(card_owner); valid = false }

      // Button changes according to output for feedback
      if(valid){
        document.getElementById("loading").style.display = "block";
        document.getElementById("proccess").innerHTML = "Processing, please wait"
        document.getElementById("proccess").style.background = "#002f86"
        document.getElementById("proccess").disabled = true;
      }
      else
      {
        document.getElementById("proccess").innerHTML = "Information not valid, try again"
        document.getElementById("proccess").style.background = "#D8000C"
      }

      // Returning result of validation
      return valid;
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

function validateExpDate(exp) {
    var date = exp.split('/');

    if (date.length == 2)
    {
      var month = parseInt(date[0]);
      var year = parseInt(date[1]) + 2000;

      if( year  >= (new Date()).getFullYear())
      {
        if( year == (new Date()).getFullYear() )
        {
          if( month > (new Date()).getMonth() ) return true;
        }
        else return true;
      }
    }

    return false;
};

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
};

function validateContent(content){
    return content != "";
};

// THIS CODE IS NOT MINE
// SOURCE: https://gist.github.com/DiegoSalazar/4075533
// ALL CREDITS TO DiegoSalazar: https://github.com/DiegoSalazar
// takes the form field value and returns true on valid number
function valid_credit_card(value) {
  // accept only digits, dashes or spaces
	if (/[^0-9-\s]+/.test(value)) return false;

	// The Luhn Algorithm. It's so pretty.
	var nCheck = 0, nDigit = 0, bEven = false;
	value = value.replace(/\D/g, "");

	for (var n = value.length - 1; n >= 0; n--) {
		var cDigit = value.charAt(n),
			  nDigit = parseInt(cDigit, 10);

		if (bEven) {
			if ((nDigit *= 2) > 9) nDigit -= 9;
		}

		nCheck += nDigit;
		bEven = !bEven;
	}

	return (nCheck % 10) == 0;
}
