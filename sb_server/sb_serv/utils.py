from math import radians, sin, cos, atan2, sqrt
EARTH_RADIUS = 6378100

def lat_lon_dist(lat1, lon1, lat2, lon2):
	""" Computes the distance, in metres, between the points (lat1, lon1) and (lat2, lon2) """
	lat1_rad = radians(lat1)
	lat2_rad = radians(lat2)

	d_lat = radians(lat2 - lat1)
	d_lon = radians(lon2 - lon1)

	s1 = sin(0.5*d_lat)
	s2 = sin(0.5*d_lon)

	a = s1*s1 + cos(lat1_rad) * cos(lat2_rad) * s2 * s2
	c = 2 * atan2(sqrt(a), sqrt(1 - a))
	return EARTH_RADIUS * c
