import json

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import timezone

# FOR DEV
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST

from sb_serv.models import User, Course, Conversation
from sb_serv.utils import lat_lon_dist

# GCM
from gcm import GCM
from sb_server.settings import GCM_API_KEY
gcm = GCM(GCM_API_KEY)

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
		reg_id = request.POST['reg_id']
	except KeyError:
		return HttpResponse('Bad request', status=400)

	user = User(name=user_name, email=user_email, reg_date=timezone.now(), reg_id=reg_id, lat=0, lon=0)
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

@csrf_exempt
def close_users(request, courses, lat, lon, dist):
	""" Returns a JSON list of users in the given (comma-separated) list of courses, within
		the given distance of the coordinates. """
	lat = float(lat)
	lon = float(lon)
	dist = int(dist)

	course_codes = courses.split(',')
	courses = [course for course in Course.objects.filter(code__in=course_codes)]

	response_data = {}
	for course in courses:
		near_users_json = []
		# Filter users based on distance
		for user in course.users.all():
			if lat_lon_dist(lat, lon, user.lat, user.lon) <= dist:
				near_users_json.append(user.json_dict())
		response_data[course.code] = near_users_json

	return HttpResponse(json.dumps(response_data), content_type='application/json')

@csrf_exempt
@require_POST
def new_conversation(request):
	try:
		name = request.POST['name']
	except KeyError:
		return HttpResponse('Bad data', status=400)

	c = Conversation(name=name, created_time=timezone.now())
	c.save()

	return HttpResponse(c.id)

@csrf_exempt
def list_conversations(request):
	response_data = [{'name': c.name, 'users': [u.json_dict() for u in c.users.all()]}
		for c in Conversation.objects.all()]
	return HttpResponse(json.dumps(response_data), content_type='application/json')

@csrf_exempt
@require_POST
def add_to_conversation(request):
	try:
		conv_id = int(request.POST['conv_id'])
		user_id = int(request.POST['user_id'])
	except KeyError:
		return HttpResponse('Bad data', status=400)

	conv = Conversation.objects.get(pk=conv_id)
	user = User.objects.get(pk=user_id)

	conv.users.add(user)

	return HttpResponse()

@csrf_exempt
@require_POST
def post_message(request):
	try:
		conv_id = int(request.POST['conv_id'])
		author = request.POST['author']
		content = request.POST['content']
	except KeyError:
		return HttpResponse('Bad data', status=400)

	data = {
		'the_message': content,
		'author': author,
		'conversation_id': str(conv_id)
	}

	conv = Conversation.objects.get(pk=conv_id)
	reg_ids = [user.reg_id for user in conv.users.all()]

	gcm.json_request(registration_ids=reg_ids, data=data)

	return HttpResponse()
