# Family Tree Application — Developer Setup Guide

## Table of Contents
1. [Prerequisites](#1-prerequisites)
2. [Clone Repositories](#2-clone-repositories)
3. [Database Setup](#3-database-setup)
4. [Application Configuration](#4-application-configuration)
5. [Build the Application](#5-build-the-application)
6. [Run the Application](#6-run-the-application)
7. [UX Development Setup](#7-ux-development-setup)

---

## 1. Prerequisites

Install the following tools before starting:

| Tool | Version | Notes |
|------|---------|-------|
| PostgreSQL | 14+ | Include pgAdmin during installation |
| pgAdmin | 4+ | Bundled with PostgreSQL installer |
| JDK | 17+ | Set `JAVA_HOME` environment variable |
| Git CLI | any | Required to clone repositories |
| IntelliJ IDEA or Eclipse | any | Java IDE for backend development |
| Visual Studio Code | any | For UX (React) development |
| Node.js + npm | 18+ | Required for building the React UX |

---

## 2. Clone Repositories

All project code must reside under a single parent folder. Create a folder named `rajput` in your home directory and clone all three repositories into it.

```bash
mkdir ~/rajput
cd ~/rajput

git clone https://github.com/vijaygarry/neasaa-base-app.git
git clone https://github.com/vijaygarry/family-tree.git
git clone https://github.com/vijaygarry/family-tree-ux.git
```

Your directory structure should look like this:

```
~/rajput/
  neasaa-base-app/    ← Shared base library (used by backend build)
  family-tree/        ← Backend Java application (this repo)
  family-tree-ux/     ← React frontend
```

---

## 3. Database Setup

All database scripts are located under `family-tree/source/components/database/`.

### 3.1 Create Database Users

Open `shared-web/dbsetup/create-db-user.sql` and replace the placeholder passwords before running:  
Replace `random-password` password with actual password. Reachout to other developer to get the dev passwords.

| User | Default (dev only) | Purpose |
|------|--------------------|---------|
| `familytree_master` | `<DevDBOwnerPwd>` | DB owner, not used by the app |
| `familytree_app_user` | `<DevDBAppPwd>` | Used by the application |
| `replicator` | `replicator` | Replication only |

> **Note:** Use strong passwords in non-development environments.

Open PG Admin and login with the PostgreSQL superuser (`postgres`):

Open the following file in PG Admin and execute the sql.
```
~/rajput/family-tree/source/components/database/shared-web/dbsetup/create-db-user.sql
```

### 3.2 Create the Database

Open the following file in PG Admin and execute the sql.
```
~/rajput/family-tree/source/components/database/shared-web/dbsetup/create-db.sql
```
This creates the `family_tree` database owned by `familytree_master`.

### 3.3 Create Schemas and import data

```
./shared_schema-dev.sql
```

---

## 4. Application Configuration

All config files that need local edits are under:
`family-tree/source/components/family-tree-web-boot/src/main/config/`

### 4.1 local.properties

File: `family-tree/source/local.properties`

Set the absolute paths to the sibling repositories on your machine:

```properties
neasaaBaseAppPath=/<base-dir>/rajput/neasaa-base-app
uxComponentRootPath=/<base-dir>/rajput/family-tree-ux
```

### 4.3 rajput.properties

File: `src/main/config/rajput.properties`

Update the paths and email settings for your local environment:

```properties
# Directory where uploaded member/family images are stored
app.upload.dir=/<base-dir>/rajput/uploaded-images/

# Tomcat access log output directory
server.tomcat.accesslog.directory=/<base-dir>/rajput/logs

# Email server credentials (needed for OTP and registration emails)
email.server.username=<gmail-address>
email.server.password=<gmail-app-password>
```

Create the required directories:

```bash
mkdir -p ~/rajput/uploaded-images
mkdir -p ~/rajput/logs
```

---

## 5. Build the Application

All Gradle commands must be run from the `source/` directory:

```bash
cd ~/rajput/family-tree/source
```

**Build backend only (fast):**
```bash
./gradlew build
```

**Build including the React UX (slower — copies built UX into the Spring Boot static folder):**
```bash
./gradlew build -PbuildReactApp
```

---

## 6. Run the Application

From the `source/` directory:

```bash
./gradlew :components:family-tree-web-boot:runApp
```

The application starts on **http://localhost:8080**.

---

## 7. UX Development Setup

The React frontend lives in the `family-tree-ux` repository.

```bash
cd ~/rajput/family-tree-ux

# Install dependencies
npm install

# Start local dev server (hot reload, proxies API calls to localhost:8080)
npm start
```

The UX dev server runs on **http://localhost:3000** and expects the backend to be running at `localhost:8080`.

**To do a production-style build and copy output into the backend:**
```bash
npm run rebuild
```

Or trigger it from the backend build:
```bash
cd ~/rajput/family-tree/source
./gradlew build -PbuildReactApp
```

---