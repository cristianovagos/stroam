$(document).ready(function () {
   onRemoveShoppingCart();
   onCancelOrder();

   function onRemoveShoppingCart() {
       var trigger = $("body").find('[data-target="#removeModal"]');
       trigger.click(function () {
           var modal = $(this).data("target"),
               productID = $(this).data("productid"),
               seasonID = $(this).data('seasonid'),
               csrfToken = $('input[name="csrfmiddlewaretoken"]').val();
           $(modal + ' button.btn.btn-danger').click(function () {
               $.post('/cart/', {
                   csrfmiddlewaretoken: csrfToken,
                   productID: productID,
                   seasonID: seasonID
               }, function () {
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

function onWatchMovieClick() {
   streamSetup();
   $("#watchMovie").modal('show');
}

// NOTIFICATION HANDLERS

var subscribeSocket = new WebSocket(
    'ws://' + window.location.host + '/ws/subscribe/'
);

subscribeSocket.onopen = function (e) {
    console.log("subscribe socket opened");
};

subscribeSocket.onerror = function (e) {
    console.error("subscribe socket error", e);
};

subscribeSocket.onmessage = function (e) {
    var data = JSON.parse(e.data);
    console.log("data received on socket: ", data);
};

subscribeSocket.onclose = function (e) {
    console.error("Subscribe socket closed unexpectedly", e);
};

function subscribeToChannel(channel_name) {
    subscribeSocket.send(JSON.stringify({
        'channel': channel_name
    }));
}