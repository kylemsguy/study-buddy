import json

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import timezone

# FOR DEV
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST

from sb_serv.models import User


@csrf_exempt
def serv_post(request):
	return render(request, 'serv_post.html')

@csrf_exempt
def list_users(request):
	""" Outputs a JSON list of all users with relevant information """
	response_data = [user.json_dict() for user in User.objects.all()]
	return HttpResponse(json.dumps(response_data), content_type='application/json')

@csrf_exempt
@require_POST
def register_user(request):
	""" Registers a new user with the given username and email """
	try:
		user_name = request.POST['user_name']
		user_email = request.POST['user_email']
	except KeyError:
		return HttpResponse('Bad data', status=400)

	user = User(name=user_name, email=user_email, reg_date=timezone.now(), lat=0, lon=0)
	user.save()

	return HttpResponse(json.dumps(user.json_dict()))

@csrf_exempt
@require_POST
def add_courses(request):
	""" Adds the given user to the given courses in a comma-separated list of course codes,
		creating the courses if they do not already exist. """

	return HttpResponse()
