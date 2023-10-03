# TypeDI

This is a small lightweight dependency injection tool inspired by the syntax of 
the typescript framework [typedi](https://github.com/typestack/typedi)

# Installation

# Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.Inventex-Development</groupId>
    <artifactId>TypeDI</artifactId>
    <version>1.0.3</version>
</dependency>
```

# Gradle
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```gradle
dependencies {
    implementation 'com.github.Inventex-Development:TypeDI:1.0.3'
}
```

## Dependency injection using constructors

```java
import dev.inventex.typedi.Service;

@Service
public class ExampleInjectedService {
    public void sayHello() {
        System.out.println("Hello World");
    }

    public int getValue() {
        return 42;
    }
}

@Service()
public class ExampleService {
    public final int value;

    public ExampleService(ExampleInjectedService injectedService) {
        injectedService.sayHello();
        this.value = injectedService.getValue();
    }
}
```

```java

import dev.inventex.typedi.Container;

public class Main {
    public static void main(String[] args) {
        ExampleService service = Container.get(ExampleService.class);
        System.out.println(service.value);
    }
}
```

## Dependency injection using fields

```java
import dev.inventex.typedi.Service;

@Service
public class ExampleInjectedService {
    public void sayHello() {
        System.out.println("Hello World");
    }
}

@Service
public class ExampleService {
    @Inject
    public ExampleInjectedService injectedService;
}
```

```java
public class Main {
    public static void main(String[] args) {
        ExampleService service = Container.get(ExampleService.class);
        service.injectedService.sayHello();
    }
}
```

## Using global services

```java
import dev.inventex.typedi.Service;

@Service(global = true)
public class MyGlobalService {
    private int value;

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        MyGlobalService service = Container.get(MyGlobalService.class);
        MyGlobalService service2 = Container.get(MyGlobalService.class);
        
        assert service == service2;
        
        service.increment();
        
        assert service.getValue() == service2.getValue();
    }
}
```

## Using separated container environments

```java
import dev.inventex.typedi.Service;

@Service(global = true)
public class ExampleService {
    private int value;

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }
}
```

```java
import dev.inventex.typedi.Container;

public class Main {
    public static void main(String[] args) {
        ExampleService production = Container.of("production").get(ExampleService.class);
        ExampleService development = Container.of("development").get(ExampleService.class);

        production.increment();

        assert production != development;
        assert production.getValue() != development.getValue();
    }
}
```

## Using global variables

```java
import dev.inventex.typedi.Container;

public class Config {
    public void init() {
        Container.set("MYSQL_HOST", "localhost");
        Container.set("MYSQL_PORT", 3306);
    }
}

public class Database {
    public void connect() {
        String host = Container.get("MYSQL_HOST");
        int port = Container.get("MYSQL_PORT");

        // ...
    }
}
```

## Using factories to instantiate services

```java
import dev.inventex.typedi.Service;

public class MyServiceFactory implements Factory<MyService> {
    public MyService create() {
        MyService service = new MyService();
        service.setSecret("shush!");
        return service;
    }
}

@Service(factory = MyServiceFactory.class)
public class MyService {
    private String secret;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}

public class Main {
    public static void main(String[] args) {
        MyService service = Container.get(MyService.class);
        assert service.getSecret().equals("shush!");
    }
}
```
