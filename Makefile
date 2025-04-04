SERVER_RESOURCES := server/src/main/resources/static

.PHONY: all clean build-client build-host-client copy-client copy-host-client build-server

all: clean build-client build-host-client copy-client copy-host-client build-server

clean:
	rm -rf client/dist
	rm -rf host-client/dist
	rm -f $(SERVER_RESOURCES)/assets/*.{js,css}
	rm -f $(SERVER_RESOURCES)/{client,host-client}.html
	rm -rf server/build

build-client:
	cd client && bun install && bun run build

build-host-client:
	cd host-client && bun install && bun run build

copy-client:
	cp client/dist/index.html $(SERVER_RESOURCES)/client.html
	cp -r client/dist/assets/* $(SERVER_RESOURCES)/assets/

copy-host-client:
	cp host-client/dist/index.html $(SERVER_RESOURCES)/host-client.html
	cp -r host-client/dist/assets/* $(SERVER_RESOURCES)/assets/

build-server:
	cd server && ./gradlew build
