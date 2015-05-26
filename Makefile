ifdef PRODUCTION
PRODUCTION=true
include .prod.env
else
include .env
endif

db-init:
	curl -k -X PUT ${COUCH_ADMIN_DB_URL}
	curl -k -X PUT ${COUCH_ADMIN_URL}/_users/org.couchdb.user:${COUCH_USERNAME} \
		-H "Content-Type: application/json" \
		-d '{"name":"${COUCH_USERNAME}", \
			"password":"${COUCH_PASSWORD}", \
			"roles":[], "type":"user"}'
	curl -k -X PUT ${COUCH_ADMIN_DB_URL}/_security \
		-H "Content-Type: application/json" \
		-d '{"admins":{"names":["${COUCH_ADMIN_USERNAME}"]}, \
			"members":{"names":["${COUCH_USERNAME}"]}}'
	export NODE_PATH=${NODE_PATH} && \
		export NODE_TLS_REJECT_UNAUTHORIZED=0 && \
		couchapp push app.js ${COUCH_ADMIN_DB_URL}
db-drop:
	curl -k -X DELETE ${COUCH_ADMIN_DB_URL}
db-seed:
	cd demo-data && for f in $$(ls *.json); do \
		curl -k -X PUT -d @$${f} ${COUCH_URL}/$$(uuidgen); done
db-reseed: db-drop db-init db-seed

dev:
	foreman start

couch-test-1:
	curl -k ${COUCH_URL}/_design/app/_view/articles?startkey=\"2015-05-22T10:43:10.441Z\"&endkey=\"2015-05-22T10:43:10.441Z\"
couch-test-2:
	curl -k ${COUCH_URL}/_design/app/_view/articles
couch-test-3:
	curl -k ${COUCH_URL}/_design/app/_view/articles?limit=1

# NOOK STUFF
ADB = ${ANDROID_HOME}/platform-tools/adb
EMULATOR = ${ANDROID_HOME}/tools/emulator
client-emulator:
	nohup ${EMULATOR} -avd nook-simple-touch -wipe-data > emulator.log 2>&1 &
	${ADB} wait-for-device
client-logs:
	${ADB} shell logcat
client-deploy:
	cd nook && ./gradlew --daemon installDebug
client-deploy-release:
	@[ "${PRODUCTION}" = "true" ]
	cd nook && ./gradlew --daemon installRelease
client-to-device:
	@[ "${PRODUCTION}" = "true" ]
	@[ -d '/Volumes/NOOK' ] || ( echo "Nook not attached." && exit 1 )
	cd nook && ./gradlew --daemon installRelease
	diskutil eject /Volumes/NOOK
	diskutil eject /Volumes/NO\ NAME
client-screen-visible:
	echo 'window scale 0.9' | nc localhost 5554
client-screen-correct:
	echo 'window scale 1.0' | nc localhost 5554

firefox-dev:
	cd firefox && cfx --static-args="{\"COUCH_URL\":\"${COUCH_URL_FOR_FUSSY_APPS}\"}" run \
		--binary-args="-url http://www.theatlantic.com/national/archive/2015/05/john-nashs-beautiful-life/394061/"
firefox-package:
	-mkdir .attachments
	cd firefox && \
		cfx --static-args="{\"COUCH_URL\":\"${COUCH_URL_FOR_FUSSY_APPS}\"}" xpi \
			--update-url=${COUCH_URL}/_design/app/offliner.update.rdf \
			--update-link=${COUCH_URL}/_design/app/offliner.xpi
		cp firefox/offliner.xpi .attachments/
		cp firefox/offliner.update.rdf .attachments/
	@echo "Firefox packaging complete.  XPI and RDF available in './firefox/'."
