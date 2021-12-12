# File storage

#### An implementation of simple client-server application for uploading/downloading files. ###

---

## Overview

This application consists of two parts:
- A client, which can select a file and upload it to server. A client can download the previously uploaded file from the server. 
  **Note: Clients encrypt a file before uploading it to the server. Once downloaded from the
  server, the client decrypts the file.**
- A server, which receives files from clients, and serves the files back to them upon request.

## Demo
![alt text](https://github.com/amonin7/file-storage/blob/main/demo/uploadFile.png)
![alt text](https://github.com/amonin7/file-storage/blob/main/demo/watchFiles.png)
![alt text](https://github.com/amonin7/file-storage/blob/main/demo/downoadFile.png)

## Prerequisites

- Java 11
- [Spring Boot 2.6.0](https://projects.spring.io/spring-boot)
- [Gradle 7.2](https://gradle.org/) - Dependency Management
- HTML5
- jquery 3.4.1

## Installation guide

**1. Clone the repository**
```bash
git clone https://github.com/amonin7/file-storage.git
```

**2a. Run the app using maven**
```bash
cd file-storage
gralde clean build bootRun
```

**2b. Run the app manually**
- Open the project `file-storage` in a specific IDE
- Right-click on the class `FileStorageApplication`
- Choose `Run 'FileStorageApplication'`

**3. Use the app**

The application can be accessed at http://localhost:8080/

## 