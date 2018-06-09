from django.http import HttpResponse
from django.shortcuts import render
from rest_framework import mixins, status
from rest_framework.response import Response

from REST.models import *
from REST.serializer import PlantSerializer, PlantLogSerializer,LEDSerializer

# Create your views here.
from rest_framework.generics import GenericAPIView


def str2Bool(v):
    if v == 'false':
        return False
    if v == 'true':
        return True


class PlantAPI(GenericAPIView, mixins.ListModelMixin):
    serializer_class = PlantSerializer

    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

    def get_queryset(self):
        queryset = Plant.objects.all()
        device_token = self.request.query_params.get('device_token', None)
        plant_id = self.request.query_params.get('id',None)
        if device_token is not None:
            queryset = queryset.filter(device_token=device_token)
        if plant_id is not None:
            queryset = queryset.filter(id=plant_id)
        return queryset

    def post(self, request, *args, **kwargs):
        if request.POST['name']:
            is_on = request.POST['is_led_active']
            obj = Plant.objects.create(name=request.POST['name'], kind=request.POST['kind'], device_token=request.POST['device_token'])
            obj.is_led_active = str2Bool(is_on)
            obj.save()
            return Response(data='PLANT IS SUCCESSFULLY CREATED', status=status.HTTP_201_CREATED)
        else:
            return Response(data='PlANT NAME IS EMPTY', status=status.HTTP_400_BAD_REQUEST)


class LogAPI(GenericAPIView, mixins.ListModelMixin):
    serializer_class = PlantLogSerializer

    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

    def get_queryset(self):
        queryset = PlantLog.objects.all()
        plant_id = self.request.query_params.get('id', None)
        num = self.request.query_params.get('num',None)
        if plant_id is not None:
            queryset = queryset.filter(plant_id=plant_id).order_by('-created_date')

        if num == 1 or num == '1':
            queryset = queryset[:1]
        return queryset

    def post(self, request, *args, **kwargs):
        if request.POST['id']:
            plant = Plant.objects.get(id=request.POST['id'])
            PlantLog.objects.create(plant=plant, temperature=request.POST['temperature'], humidity=request.POST['humidity'], brightness=request.POST['brightness'],soil_water=request.POST['soil_water'])
            return Response(data='LOG IS SUCCESSFULLY CREATED', status=status.HTTP_201_CREATED)
        else:
            return Response(data='PLANT ID IS EMPTY', status=status.HTTP_400_BAD_REQUEST)


class LedAPI(GenericAPIView, mixins.ListModelMixin):
    serializer_class = LEDSerializer

    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

    def get_queryset(self):
        queryset = Plant.objects.all()
        plant_id = self.request.query_params.get('id', None)
        if plant_id is not None:
            queryset = queryset.filter(id=plant_id)
        return queryset

    def post(self, request, *args, **kwargs):
        if request.POST['id']:
            is_on = request.POST['is_led_active']
            obj = Plant.objects.get(id=request.POST['id'])
            obj.is_led_active = str2Bool(is_on)
            obj.save()
            return Response(data='LED STATUS IS UPDATED', status=status.HTTP_201_CREATED)
        else:
            return Response(data='PlANT ID IS EMPTY', status=status.HTTP_400_BAD_REQUEST)


