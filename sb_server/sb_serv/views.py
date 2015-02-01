import json

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import timezone

# FOR DEV
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST

from sb_serv.models import User, Course


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
		return HttpResponse('Bad request', status=400)

	user = User(name=user_name, email=user_email, reg_date=timezone.now(), lat=0, lon=0)
	user.save()

	return HttpResponse(json.dumps(user.json_dict()))

@csrf_exempt
@require_POST
def update_coords(request):
	""" Update the given user's coordinates """
	try:
		user_id = request.POST['user_id']
		lat = request.POST['lat']
		lon = request.POST['lon']
	except KeyError:
		return HttpResponse('Bad request', status=400)

	try:
		user = User.objects.get(pk=int(user_id))
	except User.DoesNotExist:
		return HttpResponse('No such user', status=400)

	user.lat = float(lat)
	user.lon = float(lon)
	user.save()
	
	return HttpResponse()

@csrf_exempt
def list_courses(request):
	""" Outputs a JSON list of all courses with relevant information """
	response_data = [course.json_dict() for course in Course.objects.all()]
	return HttpResponse(json.dumps(response_data), content_type='application/json')

@csrf_exempt
@require_POST
def add_courses(request):
	""" Adds the given user to the given courses in a comma-separated list of course codes,
		creating the courses if they do not already exist. """

	try:
		user_id = request.POST['user_id']
		courses_str = request.POST['courses']
	except KeyError:
		return HttpResponse('Bad data', status=400)

	try:
		user = User.objects.get(pk=int(user_id))
	except User.DoesNotExist:
		return HttpResponse('No such user', status=400)

	course_codes = courses_str.split(',')

	# Fetch existing courses
	courses = {course.code: course for course in Course.objects.filter(code__in=course_codes)}

	# Create new courses
	for code in course_codes:
		if code not in courses:
			courses[code] = Course(code=code)
			courses[code].save()

	# Add user to selected courses
	for code in courses:
		if user not in courses[code].users.all():
			courses[code].users.add(user)

	return HttpResponse(json.dumps([code for code in courses]), content_type='application/json')
