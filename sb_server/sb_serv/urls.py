from django.conf.urls import patterns, include, url

from sb_serv import views

urlpatterns = patterns('',
    url(r'^serv_post/$', views.serv_post, name='serv_post'),
    url(r'^list_users/$', views.list_users, name='list_users'),
    url(r'^register_user/$', views.register_user, name='register_user'),
    url(r'^add_courses/$', views.add_courses, name='add_courses'),
)
