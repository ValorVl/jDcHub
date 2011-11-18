#   Basic  Information 0.1a
#   By Toast 08-11-15
#
#   Prints out Hostname and PID for process
#   Simple script (example for DSHub)
#  
#   Changes 0.1a
#   Added support for log (basic_info.log)
#   Added working directory,  Java/Platform Version and user id to this version
#   Added Garbage Collector
import sys, socket, os, sys, platform, gc, time
gc.enable()
print "Basic Info:","\n"
hostname = socket.gethostname()
print "Hostname:", hostname
print "Platform:", platform.platform(aliased=1, terse=0)
pathname = os.path.dirname(sys.argv[0])
print 'Working Directory:', sys.argv[0] ,os.path.abspath(pathname)
print 'Parent Process ID:', os.getpid()
print 'Real User ID:', os.getuid()
print "Saving to information to basic_info.log"
f = open('basic_info.log', 'w')
print >> f, "Basic Info:","\n"
print >> f, "Hostname:", hostname
print >> f, "Platform:", platform.platform(aliased=1, terse=0)
print >> f, 'Working Directory:', sys.argv[0] ,os.path.abspath(pathname)
print >> f, 'Parent Process ID:', os.getpid()
print >> f, 'Real User ID:', os.getuid()
print >> f, ""
gmt = time.gmtime(time.time())
fmt = '%a, %d %b %Y %H:%M:%S GMT'
str = time.strftime(fmt, gmt)
hdr = str
print >> f, "Log Generated at:", hdr
f.close()