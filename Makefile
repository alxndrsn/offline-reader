include .env

db-init:
	curl -X PUT ${COUCH_URL}
db-drop:
	curl -X DELETE ${COUCH_URL}
db-seed:
	cd demo-data && for f in $$(ls *.json); do \
		curl -X PUT -d @$${f} ${COUCH_URL}/$$(uuidgen); done
db-reseed: db-drop db-init db-seed

dev:
	foreman start

couch-test-1:
	curl ${COUCH_URL}/_design/app/_view/articles?startkey=\"2015-05-22T10:43:10.441Z\"&endkey=\"2015-05-22T10:43:10.441Z\"
couch-test-2:
	curl ${COUCH_URL}/_design/app/_view/articles
couch-test-3:
	curl ${COUCH_URL}/_design/app/_view/articles?limit=1

# NOOK STUFF
ADB = ${ANDROID_HOME}/platform-tools/adb
EMULATOR = ${ANDROID_HOME}/tools/emulator
client-emulator:
	nohup ${EMULATOR} -avd nook-simple-touch &
	${ADB} wait-for-device
client-logs:
	${ADB} shell logcat
client-deploy:
	cd nook && ./gradlew --daemon installDebug
client-to-device:
	@[ -d '/Volumes/NOOK' ] || ( echo "Nook not attached." && exit 1 )
	cd nook && ./gradlew --daemon installRelease
	diskutil eject /Volumes/NOOK
	diskutil eject /Volumes/NO\ NAME
client-screen-visible:
	echo 'window scale 0.9' | nc localhost 5554
client-screen-correct:
	echo 'window scale 1.0' | nc localhost 5554

firefox-dev:
	cd firefox && cfx run
