  #!/bin/bash
  set -e

  cd backend || exit 1
  mvn clean package -DskipTests
  cd ..

  docker-compose up -d

  if ! docker ps | grep -q "tomcat_app"; then
    echo "error: tomcat_app container is not running!"
    exit 1
  fi

  docker cp backend/target/CurrencyExchange-1.0-SNAPSHOT.war tomcat_app:/usr/local/tomcat/webapps/currency-exchange.war

  docker restart tomcat_app

  echo "Deploy success!"
