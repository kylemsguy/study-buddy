from django.conf.urls import patterns, include, url
from django.contrib import admin

from sb_serv import views

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'sb_server.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^sb/', include('sb_serv.urls')),
    url(r'', include('gcm.urls')),
)

