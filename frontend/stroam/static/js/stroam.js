$(document).ready(function () {
   onRemoveShoppingCart();
   onCancelOrder();

   function onRemoveShoppingCart() {
       var trigger = $("body").find('[data-target="#removeModal"]');
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

   function onCancelOrder() {
       var trigger = $("body").find('[data-target="#cancelModal"]');
       trigger.click(function () {
           var modal = $(this).data("target"),
               orderID = $(this).data("orderid"),
               csrfToken = $('input[name="csrfmiddlewaretoken"]').val();
           $(modal + ' button.btn.btn-danger').click(function () {
               $.post('/userpanel/', {
                   csrfmiddlewaretoken: csrfToken,
                   orderID: orderID
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

   $('.btn-filter').on('click', function () {
      var $target = $(this).data('target');
      if ($target != 'all') {
        $('.table tbody tr').attr('style', 'display: none !important');
        $('.table tbody tr[data-status="' + $target + '"]').fadeIn('slow');
      } else {
        $('.table tbody tr').attr('style', 'display: none !important').fadeIn('slow');
      }
    });
});
