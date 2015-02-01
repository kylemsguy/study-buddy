from django.conf.urls import patterns, include, url

from sb_serv import views

urlpatterns = patterns('',
    url(r'^serv_post/$', views.serv_post, name='serv_post'),
    url(r'^list_users/$', views.list_users, name='list_users'),
    url(r'^register_user/$', views.register_user, name='register_user'),
    url(r'^update_coords/$', views.update_coords, name='update_coords'),
    url(r'^list_courses/$', views.list_courses, name='list_courses'),
    url(r'^add_courses/$', views.add_courses, name='add_courses'),
    url(r'^close_users/(?P<courses>[a-zA-Z0-9,]+)/(?P<lat>-?\d+\.?\d*)/(?P<lon>-?\d+\.?\d*)/(?P<dist>\d+)/$',
    	views.close_users, name='close_users'),
    url(r'^new_conversation/$', views.new_conversation, name='new_conversation'),
    url(r'^list_conversations/$', views.list_conversations, name='list_conversations'),
    url(r'^add_to_conversation/$', views.add_to_conversation, name='add_to_conversation'),
    url(r'^post_message/$', views.post_message, name='post_message'),
)
