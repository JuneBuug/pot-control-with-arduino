from django.contrib import admin
from django.urls import path

from .views import *

urlpatterns = [
    path('plant', PlantAPI.as_view()),
]
