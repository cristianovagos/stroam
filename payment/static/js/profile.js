

var info = {};
var selected_ba = 0;
var selected_cc = 0;

window.onload = updateData;



function updateData(){

  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user';
  Http.open("GET", url);
  Http.send();
  Http.onreadystatechange=(e)=>{
    if (Http.readyState == 4 && Http.status == 200){
      // Parsing response
      info = JSON.parse(Http.responseText);

      if(!('ERROR' in info)){
        // Filling basic information
        document.getElementById("name").value = info['BUYER']['NAME'];
        document.getElementById("email").value = info['BUYER']['EMAIL'];
        document.getElementById("nif").value = info['BUYER']['NIF'];

        // Filling Credit Card List
        var ccDiv = document.getElementById("cc_div");
        ccDiv.innerHTML = '';
        if (info['BUYER']['CREDIT_CARDS'].length == 0) document.getElementById("cc-div").style.display = "none";
        else{
          document.getElementById("cc-div").style.display = "block";
          info['BUYER']['CREDIT_CARDS'].forEach(function(element, i) {
            var ccInfo = document.createElement("A");
            ccInfo.setAttribute('class', 'credit_card');
            ccInfo.setAttribute('href', '#');
            ccInfo.setAttribute('onclick', 'cc_changed(this)');
            ccInfo.setAttribute('index', i);
            ccInfo.innerHTML = element['NUMBER'] + ' | ' + element['EXP'];
            ccDiv.appendChild(ccInfo);
          });
          document.getElementsByClassName("credit_card")[0].click();
        }

        // Filling Billing Address List
        var baDiv = document.getElementById("ba_div");
        baDiv.innerHTML = '';
        if (info['BUYER']['BILLING_ADDRESS'].length == 0) document.getElementById("ba-div").style.display = "none";
        else{

          document.getElementById("ba-div").style.display = "block";

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
          document.getElementsByClassName("billing_address")[0].click();
        }

        // Filling merchant info if exists
        if('MERCHANT' in info){
            document.getElementById("name-merchant").value = info['MERCHANT']['NAME'];
            document.getElementById("domain").value = info['MERCHANT']['DOMAIN'];
            document.getElementById("logo").value = info['MERCHANT']['LOGO'];
            document.getElementById("token").value = info['MERCHANT']['TOKEN'];
            if(info['MERCHANT']['LOGO'] != null)
            {
              document.getElementById("logo-img").src = info['MERCHANT']['LOGO'];
            }
        }

        // Selecting the first credit card and billing address
        document.getElementById("defaultOpen").click();

      }
    }
  }
}

document.getElementById("update-button").onclick = function(){
  var name = document.getElementById("name").value;
  var nif = document.getElementById("nif").value;

  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user/client';
  Http.open("PUT", url);
  Http.setRequestHeader("Content-Type", "application/json");
  Http.send(JSON.stringify({ "NAME": name, "NIF": parseInt(nif) }));

  document.getElementById("update-button").style.visibility = "hidden";
  document.getElementById("name").readOnly = true;
  document.getElementById("nif").readOnly = true;

}

document.getElementById("update-button-merchant").onclick = function(){
  var name = document.getElementById("name-merchant").value;
  var domain = document.getElementById("domain").value;
  var logo = document.getElementById("logo").value;

  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user/merchant';
  Http.open("PUT", url);
  Http.setRequestHeader("Content-Type", "application/json");
  Http.send(JSON.stringify({ "NAME": name, "DOMAIN": domain, "LOGO": logo }));

  document.getElementById("update-button-merchant").style.visibility = "hidden";
  document.getElementById("name-merchant").readOnly = true;
  document.getElementById("domain").readOnly = true;
  document.getElementById("logo").readOnly = true;
  document.getElementById("logo-img").src = logo;
}

document.getElementById("edit-ba").onclick = function(){
  document.getElementsByName("first_name")[0].readOnly = false;
  document.getElementsByName("last_name")[0].readOnly = false;
  document.getElementsByName("country")[0].readOnly = false;
  document.getElementsByName("city")[0].readOnly = false;
  document.getElementsByName("address")[0].readOnly = false;
  document.getElementsByName("post_code")[0].readOnly = false;
  document.getElementsByName("phone")[0].readOnly = false;

  document.getElementById("edit-ba").style.display = "none";
  document.getElementById("update-ba").style.display = "block";
}

document.getElementById("update-ba").onclick = function(){
  var first_name = document.getElementsByName("first_name")[0].value;
  var last_name = document.getElementsByName("last_name")[0].value;
  var country = document.getElementsByName("country")[0].value;
  var city = document.getElementsByName("city")[0].value;
  var address = document.getElementsByName("address")[0].value;
  var post_code = document.getElementsByName("post_code")[0].value;
  var phone = document.getElementsByName("phone")[0].value;

  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user/billing_address';
  Http.open("PUT", url);
  Http.setRequestHeader("Content-Type", "application/json");
  Http.send(JSON.stringify({ "ID": selected_ba, "FIRST_NAME": first_name,
                            "LAST_NAME": last_name, "COUNTRY": country,
                            "CITY": city, "ADDRESS" : address, "POST_CODE": post_code,
                            "PHONE" : parseInt(phone)}));

  document.getElementById("edit-ba").style.display = "block";
  document.getElementById("update-ba").style.display = "none";

  document.getElementsByName("first_name")[0].readOnly = true;
  document.getElementsByName("last_name")[0].readOnly = true;
  document.getElementsByName("country")[0].readOnly = true;
  document.getElementsByName("city")[0].readOnly = true;
  document.getElementsByName("address")[0].readOnly = true;
  document.getElementsByName("post_code")[0].readOnly = true;
  document.getElementsByName("phone")[0].readOnly = true;
  updateData();
}

document.getElementById("delete-ba").onclick = function(){
  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user/billing_address';
  Http.open("DELETE", url);
  Http.setRequestHeader("Content-Type", "application/json");
  Http.send(JSON.stringify({ "ID": selected_ba }));

  document.getElementById("edit-ba").style.display = "block";
  document.getElementById("update-ba").style.display = "none";
  updateData();
}

document.getElementById("delete-cc").onclick = function(){
  var Http = new XMLHttpRequest();
  var url='http://127.0.0.1:5000/api/v1/user/credit_card';
  Http.open("DELETE", url);
  Http.setRequestHeader("Content-Type", "application/json");
  Http.send(JSON.stringify({ "ID": selected_cc }));

  updateData();
}

function cc_changed(elmnt) {
  // Filling inputs with the Credit Card Selected
  var card_number = document.getElementsByName("card_number")[0];
  var expiration = document.getElementsByName("card_exp")[0];
  cc = info['BUYER']['CREDIT_CARDS'][parseInt(elmnt.getAttribute("index"))]
  card_number.value = cc['NUMBER'];
  expiration.value = cc['EXP'];

  selected_cc = cc['ID'];
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

  selected_ba = ba['ID'];
}

function edit(element_id, isMerchant = false){
  element = document.getElementById(element_id);
  element.readOnly = false;
  showUpdateButton(isMerchant);
}

function showUpdateButton(isMerchant){
  if(isMerchant) document.getElementById("update-button-merchant").style.visibility = "visible";
  else document.getElementById("update-button").style.visibility = "visible";
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
