#!/bin/bash
RET=1
while [[ RET -ne 0 ]]; do
  echo "##########################################################"
  echo "=> Waiting for confirmation of MongoDB service startup..."
  sleep 5
  mongo admin --eval "help" >/dev/null 2>&1
  RET=$?
done
echo "=> Creating a ${MONGODB_ADMIN_USER} database and user with a password in MongoDB"
mongo admin -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD <<EOF
  use $MONGO_DATABASE
  db.createUser({user: '$MONGO_DBUSER', pwd: '$MONGO_DBUSER_PASSWORD', roles:[{role:'dbOwner', db:'$MONGO_DATABASE'}]})
EOF