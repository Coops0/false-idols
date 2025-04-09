echo "Stopping server..."
ssh root@cesium-micro 'systemctl stop false-idols-server'
echo "Stopped server, uploading fatty jar"
scp server/build/libs/false-idols-all.jar root@cesium-micro:/usr/local/bin/false-idols-server.jar
echo "Uploaded fatty jar, starting server..."
ssh root@cesium-micro 'systemctl start false-idols-server'
echo "Done"