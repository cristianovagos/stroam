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
               }, function () {
                   console.log("refreshing");
                   location.reload();
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

   $('[id^=detail-]').hide();
   $('.toggle').click(function() {
       $input = $( this );
       $target = $('#'+$input.attr('data-toggle'));
       $target.slideToggle();
   });
});

// Django Channels
// document.addEventListener('DOMContentLoaded', function() {
    // const webSocketBridge = new channels.WebSocketBridge();
    // webSocketBridge.connect('/ws/cart/');
    // webSocketBridge.listen(function(action, stream) {
    //     console.log("RESPONSE:", action, stream);
    // });
    // document.ws = webSocketBridge; /* for debugging */

    // document.querySelector('#addcart').onclick = function (e) {
    //     var cartnum = document.querySelector('#cartnum');
    //     var cartnumvalue = cartnum.innerHTML;
    //     var regExp = /\(([^)]+)\)/;
    //     var num = regExp.exec(cartnumvalue);
    //     var modal = document.querySelector("#addedCart");
    //
    //     if(num != null) {
    //         var auxNum = parseInt(num[1]);
    //         auxNum += 1;
    //         cartnum.innerHTML = 'Cart (' + auxNum + ')';
    //     } else {
    //         cartnum.innerHTML = 'Cart (1)';
    //     }
    //
    //     $("#addedCart").modal('show');
    //     // modal.modal('show');
    // };
// });
