document.getElementById("defaultOpen").click();

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
