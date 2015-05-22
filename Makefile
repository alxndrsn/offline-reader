include .env

db-init:
	curl -X PUT ${COUCH_URL}
db-drop:
	curl -X DELETE ${COUCH_URL}
db-seed:
	cd demo-data && for f in $$(ls *.json); do \
		curl -X PUT -d @$${f} ${COUCH_URL}/$$(uuidgen); done

dev:
	foreman start
