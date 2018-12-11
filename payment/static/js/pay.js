window.onload = updateData;
var info = {}
var root = "http://127.0.0.1:5000/pay/"

function updateData(){

  var Http = new XMLHttpRequest();
  var url= root + 'api/v1/user';
  Http.open("GET", url);
  Http.send();
  Http.onreadystatechange=(e)=>{
    if (Http.readyState == 4 && Http.status == 200){
      // Parsing response
      info = JSON.parse(Http.responseText);

      if(!('ERROR' in info)){
        // Filling Credit Card List
        var ccDiv = document.getElementById("cc_div");
        ccDiv.innerHTML = '';
        if (info['BUYER']['CREDIT_CARDS'].length == 0) document.getElementById("cc-div").style.display = "none";
        else{
          document.getElementById("cc-div").style.display = "block";

          var ccInfo = document.createElement("A");
          ccInfo.setAttribute('href', '#');
          ccInfo.setAttribute('onclick', 'cc_changed(this, true)');
          ccInfo.innerHTML = "New Card";
          ccDiv.appendChild(ccInfo);

          info['BUYER']['CREDIT_CARDS'].forEach(function(element, i) {
            var ccInfo = document.createElement("A");
            ccInfo.setAttribute('class', 'credit_card');
            ccInfo.setAttribute('href', '#');
            ccInfo.setAttribute('onclick', 'cc_changed(this, false)');
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

          var baInfo = document.createElement("A");
          baInfo.setAttribute('href', '#');
          baInfo.setAttribute('onclick', 'ba_changed(this, true)');
          baInfo.innerHTML = "New Billing Address";
          baDiv.appendChild(baInfo);

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


      }
    }
  }
}
