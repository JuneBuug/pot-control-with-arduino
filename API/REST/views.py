from django.http import HttpResponse
from django.shortcuts import render
from rest_framework import mixins, status
from rest_framework.response import Response

from REST.models import *
from REST.serializer import PlantSerializer, PlantLogSerializer

# Create your views here.
from rest_framework.generics import GenericAPIView


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
            obj.is_led_active = strToBool(is_on)
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
        return queryset


def strToBool(v):
    if v == 'true' or 'T' or 'True':
        return True
    else:
        return False
