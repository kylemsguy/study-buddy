from django.conf.urls import patterns, include, url

from sb_serv import views

urlpatterns = patterns('',
    url(r'^list_users/$', views.list_users, name='list_users'),
    url(r'^register_user/(?P<user_name>\w+)/(?P<user_email>\w+)$', views.register_user, name='register_user'),
)
