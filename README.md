# Requirements

## CouchDB

You'll need CouchDB, set up to run with TLS support.

# Getting started

## Create a `.env` file

Create a file called `.env` in the project root directory.  It should contain the following key-value pairs:

	# the path to your node_modules directory
	NODE_PATH=/usr/local/lib/node_modules
	# Include the following line if you are using self-signed certs for your couchdb server
	NODE_TLS_REJECT_UNAUTHORIZED=0

	# the path to your android toolkit install
	ANDROID_HOME = /Users/user/dev/android-sdk-macosx

	# the name of your couch database
	COUCH_DBNAME=offliner

	# the URL of your couch database for standard user
	COUCH_URL=http://off:youtrot@localhost:5984/offliner
	# the username for your couch database user
	COUCH_USERNAME=off
	# the password for your couch database user
	COUCH_PASSWORD=youtrot

	# the username for your couch admin user
	COUCH_ADMIN_USERNAME=admin
	# the URL of your couch server for admin user
	COUCH_ADMIN_URL=http://admin:pass@localhost:5984
	# the URL of your couch database for admin user
	COUCH_ADMIN_DB_URL=http://admin:pass@localhost:5984/offliner

## Create your database

	make db-init

This will also create the user defined by `${COUCH_USERNAME}` and
`${COUCH_PASSWORD}` in your `.env` file.

## Push the design doc to couch

	make webapp-deploy

## See it

Point your browser at the web interface served by couchdb, e.g.

	http://localhost:5984/offliner/_design
	
## Browser plugin

To start firefox with the browser plugin:

	make firefox-dev

Then browse to a page with an `<article>` tag, right-click and `Read offline`.

## Mobile client

To deploy to android:

	make client-emulator
	make client-deploy
