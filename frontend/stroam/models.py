from django.db import models
from django.utils import timezone

class Purchase(models.Model):
    INVOICE_CREATED = 1
    AWAITING_PAYMENT = 2
    PAYMENT_ERROR = 3
    PAYMENT_COMPLETED = 4

    PAYMENT_STATUS = (
        (INVOICE_CREATED, 'Invoice Created'),
        (AWAITING_PAYMENT, 'Awaiting Payment'),
        (PAYMENT_ERROR, 'Payment Error'),
        (PAYMENT_COMPLETED, 'Payment Completed')
    )

    user_id = models.IntegerField()
    token_payment = models.CharField(unique=True, max_length=50)
    payment_status = models.IntegerField(choices=PAYMENT_STATUS, default=INVOICE_CREATED)
    date_created = models.DateTimeField(default=timezone.now)
    date_payment = models.DateTimeField(blank=True, null=True)

    def awaitPayment(self):
        self.payment_status = self.AWAITING_PAYMENT
        self.save()

    def onPaymentError(self):
        self.payment_status = self.PAYMENT_ERROR
        self.save()

    def onCompletedPayment(self):
        self.payment_status = self.PAYMENT_COMPLETED
        self.date_payment = timezone.now()
        self.save()

class Purchase_Production(models.Model):
    production_id = models.IntegerField()
    purchase_id = models.ManyToManyField(Purchase)
