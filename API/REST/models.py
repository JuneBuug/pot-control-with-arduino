import uuid
from django.db import models

# Create your models here.


'''
식물 모델
'''


class Plant(models.Model):
    id = models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True)
    name = models.CharField(max_length=256)
    kind = models.CharField(max_length=256)
    is_led_active = models.BooleanField(default=False)
    device_token = models.CharField(max_length=1024)


'''
식물 로그 모델
'''


class PlantLog(models.Model):
    plant = models.ForeignKey('Plant', on_delete=models.CASCADE, null=False ,blank=False)
    created_date = models.DateTimeField(auto_now_add=True)
    temperature = models.IntegerField()  # 온도
    humidity = models.IntegerField()  # 퍼센트로
    brightness = models.IntegerField()  # 조도




