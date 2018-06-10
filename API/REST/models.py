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

    def __str__(self):
        return self.name + "(" + self.kind + ")"


'''
식물 로그 모델
'''


class PlantLog(models.Model):
    plant = models.ForeignKey('Plant', on_delete=models.CASCADE, null=False, blank=False)
    created_date = models.DateTimeField(auto_now_add=True)
    temperature = models.CharField(max_length=256)  # 온도
    humidity = models.CharField(max_length=256)  # 토양 습도 퍼센트로
    brightness = models.CharField(max_length=256) # 조도
    soil_water = models.CharField(max_length=256)  # 토양 수분 (퍼센트)

    def __str__(self):
        return self.plant.name + "(" + self.plant.kind + ") " + str(self.created_date.timestamp())


