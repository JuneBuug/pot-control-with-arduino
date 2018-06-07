from rest_framework import serializers
from REST.models import *


class PlantSerializer(serializers.ModelSerializer):
    class Meta:
        model = Plant
        fields = ['id', 'name','kind','is_led_active']


class PlantLogSerializer(serializers.ModelSerializer):
    class Meta:
        model = PlantLog
        fields = ['plant','created_date', 'temperature', 'humidity', 'brightness']