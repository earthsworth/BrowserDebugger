# Browser Debugger

[![Discord](https://img.shields.io/discord/1047866655033802802?label=Discord)](https://discord.lunarclient.top)

The 1# javaagent for browsers, especially for Chrome.

***Star us right now!***

## Usage

- Get Celestial Launcher
- Put this agent into the javaagent folder
- Launch your browser

## Advanced Usage

There are some params to let you use the custom backends in the browser

- [Use another self-hosted browser-api backend](#use-another-self-hosted-browser-api-backend)
- [Use the Service Override Properties](#use-the-service-override-properties)
- [Use the Proprietary Service (prod)](#use-the-proprietary-service-production)
- [Use the Proprietary Service (dev)](#use-the-proprietary-service-dev)

### Use another self-hosted browser-api backend

Pass browserVM params `-DcelestialApiAddress=<address>` to define the api host, but don't special the protocol

Pass browserVM params `-DcelestialApiUseEncryption=<true/false>` to manage the encryption status (requires the backend
enable tls)

| Parameter                 | Type       | Description                                                                         | Example       |
|---------------------------|------------|-------------------------------------------------------------------------------------|---------------|
| celestialApiAddress       | properties | the host of the backend ([browser-api](https://codeberg.org/earthsworth/lunar-api)) | `example.com` |
| celestialApiUseEncryption | properties | The encryption status of the backend. Possible values are true/false                | `true`        |

To pass a property to the browser, please use the `-D<property-name>=<value>` VM option.

For Example `java -DcelestialApiAddress=example.com BrowserMain`

### Use the Service Override Properties

This is explained [here](https://codeberg.org/earthsworth/lunar-api#connect-to-the-server)

| Option        | Description                                                                    | Example                           |
|---------------|--------------------------------------------------------------------------------|-----------------------------------|
| Authenticator | The authenticator for the browser, used to valid sessions                      | ws://127.0.0.1:8080/ws            |
| AssetServer   | The RPC for the browser, used to communicate between the client and the server | ws://127.0.0.1:8080/ws            |
| Api           | The API for the browser, used to query data via REST API                       | http://127.0.0.1:8080/api/browser |
| Styngr        | The music player backend for the browser, used to play and query musics        | http://127.0.0.1:8080/api/styngr  |

Add VM parameter `-DserviceOverride<Option>=<value>` to enable the feature

### Use the Proprietary Service (Production)

> Warning: You'll be tracked by the browser company, not recommended.

- Set Javaagent parameter to `proprietary` to connect to the proprietary service

Service Override feature will still work

### Use the Proprietary Service (Dev)

> Warning: You'll be tracked by the browser company, not recommended.

- Set Javaagent parameter to `proprietary` to disable the default behaver in BrowserDebugger
- pass `-DdevServices=true` to the browser VM parameters to connect to the dev backend of the proprietary service

Service Override feature will still work
