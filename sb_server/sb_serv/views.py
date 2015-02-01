import json

from django.shortcuts import render
from django.http import HttpResponse
from django.utils import timezone

from sb_serv.models import User

def list_users(request):
	""" Outputs a JSON list of all users with relevant information """
	response_data = [user.json_dict() for user in User.objects.all()]
	return HttpResponse(json.dumps(response_data), content_type='application/json')

def register_user(request, user_name, user_email):
	""" Registers a new user with the given username and email """
	user = User(name=user_name, email=user_email, reg_date=timezone.now(), lat=0, lon=0)
	user.save()
	return HttpResponse()
