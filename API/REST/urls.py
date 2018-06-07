from django.contrib import admin
from django.urls import path

from .views import *

urlpatterns = [
    path('plant', PlantAPI.as_view()),
    path('log',LogAPI.as_view()),
]
