from django.db import models
from django.utils import timezone

class Purchase(models.Model):
    AWAITING_PAYMENT = 1
    PAYMENT_ERROR = 2
    ORDER_CANCELLED = 3
    PAYMENT_COMPLETED = 4

    PAYMENT_STATUS = (
        (AWAITING_PAYMENT, 'Awaiting Payment'),
        (PAYMENT_ERROR, 'Payment Error'),
        (ORDER_CANCELLED, 'Order Cancelled'),
        (PAYMENT_COMPLETED, 'Payment Completed')
    )

    user_id = models.IntegerField()
    token_payment = models.CharField(unique=True, max_length=50)
    token_isValid = models.BooleanField(default=True)
    payment_status = models.IntegerField(choices=PAYMENT_STATUS, default=AWAITING_PAYMENT)
    date_created = models.DateTimeField(default=timezone.now)
    date_payment = models.DateTimeField(blank=True, null=True)

    def onPaymentError(self):
        self.payment_status = self.PAYMENT_ERROR
        self.save()

    def onOrderCancelled(self):
        self.payment_status = self.ORDER_CANCELLED
        self.token_isValid = False
        self.save()

    def onCompletedPayment(self):
        if self.token_isValid:
            self.payment_status = self.PAYMENT_COMPLETED
            self.date_payment = timezone.now()
            self.save()

class Purchase_Production(models.Model):
    production_id = models.IntegerField()
    purchase_id = models.ManyToManyField(Purchase)
    season_num = models.IntegerField(blank=True, null=True)
