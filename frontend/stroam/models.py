from django.db import models

class Purchase(models.Model):
    user_id = models.IntegerField()
    token_payment = models.CharField(unique=True, max_length=20)

class Purchase_Production(models.Model):
    production_id = models.IntegerField()
    purchase_id = models.ManyToManyField(Purchase)