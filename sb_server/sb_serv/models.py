from django.db import models

from gcm.models import AbstractDevice

class SBDevice(AbstractDevice):
	pass

class User(models.Model):
	""" Stores up to date user info """
	email = models.EmailField()
	name = models.CharField(max_length = 200)
	reg_date = models.DateTimeField('date registered')

	#reg_id = models.CharField(max_length=512)
	"""
	device = models.ForeignKey(SBDevice, default=SBDevice(dev_id='test',
		reg_id='APA91bFEyqxImF6eHLz6E-rgtZycTVPQANC59-SpHe1FWzmjgAZdre40jSxWRDR3eGhnOhtdNoZLNiJSX1ypOMLcNI8jxtAHjkX5eJLUg-DNYN9H3L_X2amOo58eXac5oYftRkPDdmvxMFf0kHFtg1OZNM9zZVb5gg',
		name='Test', is_active=True))
"""

	# Most recently updated GPS coordinates
	lat = models.FloatField()
	lon = models.FloatField()

	def __str__(self):
		return self.name

	def json_dict(self):
		return {
			#'email': self.email,
			'id': self.id,
			'name': self.name,
			'reg_date': self.reg_date.__str__(),
			'lat': self.lat,
			'lon': self.lon,
			'reg_id': self.reg_id
		}

class Course(models.Model):
	""" A course """
	code = models.CharField(max_length=16)
	users = models.ManyToManyField(User)

	def __str__(self):
		return self.code

	def json_dict(self):
		return {
			'code': self.code,
			'users': [user.json_dict() for user in self.users.all()]
		}

class Conversation(models.Model):
	""" A sequential thread of messages """
	name = models.CharField(max_length = 200)
	created_time = models.DateTimeField('time created')
	users = models.ManyToManyField(User)

	def __str__(self):
		return self.name

class Message(models.Model):
	""" An individual message """
	conversation = models.ForeignKey(Conversation)
	author = models.ForeignKey(User)

	time = models.DateTimeField('time sent')
	content = models.TextField()

	def __str__(self):
		return self.content

