import json

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import timezone

# FOR DEV
from django.views.decorators.csrf import csrf_exempt

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
def register_user(request):
	""" Registers a new user with the given username and email """
	if request.method != 'POST':
		return HttpResponseForbidden('<h1>403 Forbidden</h1>');

	try:
		user_name = request.POST['user_name']
		user_email = request.POST['user_email']
	except KeyError:
		return HttpResponse('Bad data', status=400)

	user = User(name=user_name, email=user_email, reg_date=timezone.now(), lat=0, lon=0)
	user.save()

	return HttpResponse()
