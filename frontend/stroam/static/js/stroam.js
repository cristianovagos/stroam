$(document).ready(function () {
   onRemoveShoppingCart();

   function onRemoveShoppingCart() {
       var trigger = $("body").find('[data-toggle="modal"]');
       trigger.click(function () {
           var modal = $(this).data("target"),
               productID = $(this).data("productid"),
               csrfToken = $('input[name="csrfmiddlewaretoken"]').val();
           $(modal + ' button.btn.btn-danger').click(function () {
               $.post('/cart/', {
                   csrfmiddlewaretoken: csrfToken,
                   productID: productID
               });
           });
           $(modal + ' button.btn.btn-secondary').click(function () {
               $(modal + ' button.btn.btn-danger').prop("onclick", null).off("click");
           });
           $(modal + ' button.close').click(function () {
               $(modal + ' button.btn.btn-danger').prop("onclick", null).off("click");
           });
       });
   }
});