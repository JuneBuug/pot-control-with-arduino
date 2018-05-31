from django.shortcuts import render
from rest_framework import mixins
from REST.models import *
from REST.serializer import PlantSerializer

# Create your views here.
from rest_framework.generics import GenericAPIView


class PlantAPI(GenericAPIView, mixins.ListModelMixin):
    serializer_class = PlantSerializer

    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

    def get_queryset(self):
        queryset = Plant.objects.all()
        return queryset