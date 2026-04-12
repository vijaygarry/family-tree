# Deployment Guide

## 1. Deploying the Complete Application Using Terraform

*This procedure deletes and recreates the EC2 server.*

---

### 1.1. Build the Latest Code

Build with UX for distribution:

```bash
# Note: make sure you include clean task
cd /Users/vijaygarothaya/work/product/family-tree/source/
./gradlew clean buildDist -PbuildReactApp
```

---
### 1.2. Copy Latest Build and Create Application File in Terraform Folder

```bash
cd /Users/vijaygarothaya/work/product/chippaSamaj/family-tree-tf/source/env/dev/scripts
./createApplicationZip.sh
```

---

### 1.3. Destroy the Current EC2 Server

```bash
cd /Users/vijaygarothaya/work/product/chippaSamaj/family-tree-tf/source/env/dev
terraform destroy -var-file="dev.tfvars"
```

---

### 1.4. Rebuild a New EC2 Server

```bash
cd /Users/vijaygarothaya/work/product/chippaSamaj/family-tree-tf/source/env/dev
terraform apply -var-file="dev.tfvars"
```

---

### 1.5. Update DNS for the New EC2 Server

Update the **A record** for the domain `rajputchhipa.com` in Cloudflare:

[https://dash.cloudflare.com/7c440bd5833797cad9d30533c737bc85/rajputchhipa.com/dns/records](https://dash.cloudflare.com/7c440bd5833797cad9d30533c737bc85/rajputchhipa.com/dns/records)

---

### 1.6. Update the EC2 Server Hostname in Ops Tool

```bash
/Users/vijaygarothaya/work/product/chippaSamaj/family-tree-tf/source/common/scripts/application/opsTools/setEc2Env.sh
```

---

## 2. Deploy Latest Code (Without Rebuilding EC2 Server)

---

### 2.1. Build and Deploy Latest Code from local machine

```bash
cd /Users/vijaygarothaya/work/product/chippaSamaj/family-tree-tf/source/env/dev/scripts/application/opsTools/dev-tools
./buildAndDeployFromLocalServer.sh
```

---

## 3. Create a New Release in Git (Post Deployment)

After confirming deployment is successful, create a new tagged release in Git.

### 3.1. Create a New Tag

Use semantic versioning (example: `v-20251205`)

```bash
git tag -a v-20251205 -m "Release v-20251205 - production deployment"
```

---

### 3.3. Push the Tag to Remote

```bash
git push origin v-20251205
```

---

### 3.4. (Optional) Create a GitHub/GitLab Release

Go to the repository → **Releases** →
Click **Draft a new release**, select the tag, and publish it.

---

Release is now officially recorded in version history.
