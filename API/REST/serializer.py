from rest_framework import serializers
from REST.models import *


class PlantSerializer(serializers.ModelSerializer):
    class Meta:
        model = Plant
        fields = ['id', 'name','kind','is_led_active']