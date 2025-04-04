scp server/build/libs/false-idols-all.jar root@cesium-micro:/usr/local/bin/false-idols-server.jar
ssh root@cesium-micro 'systemctl restart false-idols-server'