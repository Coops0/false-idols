.PHONY: all clean build-client build-host-client copy-client copy-host-client build-server

all: clean build-client build-host-client copy-client copy-host-client build-server

clean:
    rm -r client/dist
    rm -r host-client/dist
    rm server/src/main/resources/static/assets/*.{js,css}
    rm server/src/main/resources/static/client.html server/src/main/resources/static/host-client.html
    rm -r server/build

build-client:
    cd client && bun install && bun run build

build-host-client:
    cd host-client && bun install && bun run build

copy-client:
    mkdir -p server/src/main/resources/client/
    cp client/dist/index.html server/src/main/resources/static/client.html
	cp -r client/dist/assets/* server/src/main/resources/static/assets/

copy-host-client:
    mkdir -p server/src/main/resources/host-client/
	cp host-client/dist/index.html server/src/main/resources/static/host-client.html
	cp -r host-client/dist/assets/* server/src/main/resources/static/assets/

build-server:
    cd server && ./gradlew build
