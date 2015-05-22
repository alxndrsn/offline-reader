include .env

init-db:
	curl -X PUT ${COUCH_URL}
drop-db:
	curl -X DELETE ${COUCH_URL}
start-webapp:
	cd webapp && foreman start
deploy-webapp:
	cd webapp && echo "TODO!" && exit 1
seed:
	cd demo-data && for f in $$(ls *.json); do \
		curl -X PUT -d @$${f} ${COUCH_URL}/$$(uuidgen); done
