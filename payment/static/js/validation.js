var error_div = document.getElementById("form-error");


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

  document.getElementById("proccess").onclick = function(){
      var using_old = document.getElementById("using-old");
      error_div.style.display = "none";
      // Getting the inputs to validate - Content 1
      var card_number = document.getElementsByName("card-number")[0];
      var expiration = document.getElementsByName("exp")[0];
      var cvc = document.getElementsByName("cvc")[0];
      var card_owner = document.getElementsByName("card-owner")[0];

      // Getting the inputs to validate - Content 2
      var first_name = document.getElementsByName("first_name")[0];
      var last_name = document.getElementsByName("last_name")[0];
      var country = document.getElementsByName("country")[0];
      var post_code = document.getElementsByName("post_code")[0];
      var phone = document.getElementsByName("phone")[0];

      // Hidding eventual old warnings
      hideWarning(card_number);
      hideWarning(expiration);
      hideWarning(cvc);
      hideWarning(card_owner);
      hideWarning(first_name);
      hideWarning(last_name);
      hideWarning(country);
      hideWarning(post_code);
      hideWarning(phone);

      // Validation
      var valid = true;
      var error = "";
      if(!using_old){
        if (!validateContent(card_number.value) || !valid_credit_card(card_number.value)) {
          showWarning(card_number);
          valid = false;
          error = "Invalid Credit Card Number";
          showContent1();
        }
        else if (!validateExpDate(expiration.value)) {
          showWarning(expiration);
          valid = false;
          error = "Expiration date not valid";
          showContent1();
        }
        else if (!validateContent(cvc.value)) {
          showWarning(cvc);
          valid = false;
          error = "Invalid CVC";
          showContent1();
        }
        else if (!validateContent(card_owner.value)) {
          showWarning(card_owner);
          valid = false;
          error = "Invalid Card Owner name";
          showContent1();
        }
      }

      if (!validateContent(first_name.value)) {
        showWarning(first_name);
        valid = false;
        error = "Invalid First Name";
        showContent2();
      }
      else if (!validateContent(last_name.value)) {
        showWarning(last_name);
        valid = false;
        error = "Invalid Last Name";
        showContent2();
      }
      else if (!validateContent(country.value)) {
        showWarning(country);
        valid = false;
        error = "Invalid Country";
        showContent2();
      }
      else if (!validateContent(post_code.value)) {
        showWarning(post_code);
        valid = false;
        error = "Invalid Post Code";
        showContent2();
      }
      else if (!validateContent(phone.value)) {
        showWarning(phone);
        valid = false;
        error = "Invalid Phone Number";
        showContent2();
      }

      if (error != ""){
        error_div.style.display = "block";
        document.getElementById("span-error").innerHTML = error;
      }

      // Returning result of validation
      return valid;
  };

}

// Next/Previous button actions
document.getElementById("next").onclick = function(){
    showContent2();
};

document.getElementById("back").onclick = function(){
    showContent1();
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


function showContent1(){
  document.getElementById("content-2").style.display = "none";
  document.getElementById("content-1").style.display = "block";
}

function showContent2(){
  document.getElementById("content-1").style.display = "none";
  document.getElementById("content-2").style.display = "block";
}

function showWarning(input) {
    var inputDiv = input.parentElement;
    inputDiv.classList.add("alert");
};

function hideWarning(input) {
    var inputDiv = input.parentElement;
    inputDiv.classList.remove("alert");
};

function cc_changed(elmnt, newcard) {
  var newCredit = document.getElementById("newCredit");
  var oldCredit = document.getElementById("oldCredit");
  var wallet = document.getElementById("wallet");
  var using_old = document.getElementById("using-old");

  if(!newcard){
    newCredit.style.display = "none";

    var card_number = document.getElementsByName("old-card-number")[0];
    var expiration = document.getElementsByName("old-exp")[0];
    cc = elmnt.innerHTML.split(" | ");
    card_number.value = cc[0];
    expiration.value = cc[1];

    oldCredit.style.display = "block";
    wallet.innerHTML = "Using Credit Card From Wallet";
    using_old.value = true;
  }
  else
  {
    newCredit.style.display = "block";
    oldCredit.style.display = "none";
    wallet.innerHTML = "Credit Card Wallet"
    using_old.value = false;
  }
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
