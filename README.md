# Getting started

## Create a `.env` file

Create a file called `.env` in the project root directory.  It should contain the following key-value pairs:

	# The URL of your couch database
	COUCH_URL=http://admin:pass@localhost:5984/offliner

## Create your database

	make db-init

## Push the design doc to couch

	make webapp-deploy

## See it

Point your browser at the web interface served by couchdb, e.g.

	http://localhost:5984/offliner/_design
	
