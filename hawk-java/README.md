# Hawk Java Catcher

[Hawk](https://github.com/codex-team/hawk) allows to track Java errors in your applications.

# Installation

### Import through Maven Repository

In your build.gradle file write:

```declarative
dependencies {
    implementation("so.hawk:hawkcatcher:1.0.1")
    implementation("org.json:json:20250107")
}
```

Don't forget to import Maven Repository:

```declarative
repositories {
    mavenCentral()
}
```

## Usage

### Get an Integration Token

First of all, you should register an account on [hawk.so](https://garage.hawk.so/sign-up).

Then create a Workspace and a Project in there. You'll get an Integration Token.

### Initialize Catcher

Create `Hawk` class instance when script will be ready and pass your Integration Token:

```java
Hawk.init("PASTE_YOUR_TOKEN");
```

## Manual sending

You can send errors or other messages to the Hawk manually, for example at your catch blocks or any debug conditions.

Use the .send(message) method for that. This method accepts the message of type Error or string as the first parameter. 

```java
Hawk.init("PASTE_YOUR_TOKEN");

Hawk.send("I love Hawk so much ^_^");
```

## Initial config

To config Hawk use `config` option:

```java
Hawk.init(config -> {
            config.setToken(integrationtoken)
                    .setContext("application", "PlaygroundApp")
                    .setContext("version", "1.0.0")
                    .setUser("id", "12345")
                    .setBeforeSend(payload -> {
                        payload.put("customField", "Custom Value");
                        return payload;
                    });
        });
```