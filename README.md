# crazyflie-lib-java

Crazyflie Java Lib based on the [Bitcraze Android client](https://github.com/bitcraze/crazyflie-android-client), but purely using Java code and in the form of a Gradle Project, easy to be used or customized.

## Installation

### Gradle

- Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```
	repositories {
			...
			maven { url 'https://jitpack.io' }
	}
```

- Step 2. Add the dependency

```
	dependencies {
            ...
            implementation 'com.github.andre-paraense:crazyflie-lib-java:0.1.0'
	}
```

### Maven

- Step 1. Add the JitPack repository to your build file.

```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

- Step 2. Add the dependency

```
	<dependency>
	    <groupId>com.github.andre-paraense</groupId>
	    <artifactId>crazyflie-lib-java</artifactId>
	    <version>0.1.0</version>
	</dependency>
```

### Manual

Download the latest [release](https://github.com/andre-paraense/crazyflie-lib-java/releases) and set it as a dependency in your project.

## Building the source code

This release uses gradle to download the dependencies from MavenCentral. It does not require you to have gradle installed in your system because it uses the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). Depending on your operational system, you might execute the gradlew script or the gradlew.bat script in order to compile the code. You might need the JDK to be properly installed in order to build the code. You should call "gradlew <task>" in order to build the code. Available tasks can be discovered using "gradlew tasks". After calling "gradlew build", the `crazyflie-lib-java` library will be available at build/libs directory.

## Changelog / Migrations

Follow the [release](https://github.com/andre-paraense/crazyflie-lib-java/releases) page to better understand the breaking changes of new versions.

## Example

To get started, you can take a look at our [crazyflie-meca-demo](https://github.com/andre-paraense/crazyflie-meca-demo).

