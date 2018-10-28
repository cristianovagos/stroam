

var info = {};

window.onload=function(e){

  const Http = new XMLHttpRequest();
  const url='http://127.0.0.1:5000/api/v1/user';
  Http.open("GET", url);
  Http.send();
  Http.onreadystatechange=(e)=>{
    if (Http.readyState == 4 && Http.status == 200){
      // Parsing response
      info = JSON.parse(Http.responseText);
      if(!"ERROR" in info){
        // Filling basic information
        document.getElementById("name").value = info['BUYER']['NAME'];
        document.getElementById("email").value = info['BUYER']['EMAIL'];
        document.getElementById("nif").value = info['BUYER']['NIF'];

        // Filling Credit Card List
        var ccDiv = document.getElementById("cc_div");
        info['BUYER']['CREDIT_CARDS'].forEach(function(element, i) {
          var ccInfo = document.createElement("A");
          ccInfo.setAttribute('class', 'credit_card');
          ccInfo.setAttribute('href', '#');
          ccInfo.setAttribute('onclick', 'cc_changed(this)');
          ccInfo.setAttribute('index', i);
          ccInfo.innerHTML = element['NUMBER'] + ' | ' + element['EXP'];
          ccDiv.appendChild(ccInfo);
        });

        // Filling Billing Address List
        var baDiv = document.getElementById("ba_div");
        info['BUYER']['BILLING_ADDRESS'].forEach(function(element, i) {
          var baInfo = document.createElement("A");
          baInfo.setAttribute('class', 'billing_address');
          baInfo.setAttribute('href', '#');
          baInfo.setAttribute('onclick', 'ba_changed(this)');
          baInfo.setAttribute('index', i);
          baInfo.innerHTML = [[element['FIRST_NAME'], element['LAST_NAME']].join(' '),
                              [element['ADDRESS'], element['COUNTRY']].join(', ')].join(', ');
          baDiv.appendChild(baInfo);
        });


        // Selecting the first credit card and billing address
        document.getElementById("defaultOpen").click();
        document.getElementsByClassName("credit_card")[0].click();
        document.getElementsByClassName("billing_address")[0].click();
      }
    }
  }

}

function cc_changed(elmnt) {
    // Filling inputs with the Credit Card Selected
    var card_number = document.getElementsByName("card_number")[0];
    var expiration = document.getElementsByName("card_exp")[0];
    cc = info['BUYER']['CREDIT_CARDS'][parseInt(elmnt.getAttribute("index"))]
    card_number.value = cc['NUMBER'];
    expiration.value = cc['EXP'];
};

function ba_changed(elmnt) {
  // Filling inputs with the Billing Address Selected
  var first_name = document.getElementsByName("first_name")[0];
  var last_name = document.getElementsByName("last_name")[0];
  var country = document.getElementsByName("country")[0];
  var city = document.getElementsByName("city")[0];
  var address = document.getElementsByName("address")[0];
  var post_code = document.getElementsByName("post_code")[0];
  var phone = document.getElementsByName("phone")[0];

  ba = info['BUYER']['BILLING_ADDRESS'][parseInt(elmnt.getAttribute("index"))]
  first_name.value = ba['FIRST_NAME'];
  last_name.value = ba['LAST_NAME'];
  country.value = ba['COUNTRY'];
  city.value = ba['CITY'];
  address.value = ba['ADDRESS'];
  post_code.value = ba['POST_CODE'];
  phone.value = ba['PHONE'];
}

// Function based on "How to create tabs" from w3schools
function changeTabs(evt, tabName) {
    var i, tabcontent, tablinks;
    // Hiding every tab
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    // Disable active tag
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    // Display Tab
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
}
