# GitHub Actions CI/CD

This project uses GitHub Actions for automated testing, building, and deployment.

## Workflows

### 1. CI - Build and Test (`ci-build.yml`)
**Triggers**: Push or PR to `main` or `develop` branches

**What it does**:
- ✅ Checks out code
- ✅ Sets up JDK 21 with Maven caching
- ✅ Builds both modules with `mvn clean package`
- ✅ Runs all tests
- ✅ Uploads JAR artifacts (retained for 7 days)

### 2. Docker Build and Push (`docker-build.yml`)
**Triggers**: 
- Push to `main` branch
- Version tags (`v*`)
- Manual trigger

**What it does**:
- ✅ Builds Docker images for both services
- ✅ Pushes to GitHub Container Registry (`ghcr.io`)
- ✅ Tags images with:
  - Branch name
  - Semantic version (if tagged)
  - Git SHA
- ✅ Uses layer caching for faster builds

**Image URLs**:
- `ghcr.io/YOUR_USERNAME/REPO_NAME/chatbot-service:main`
- `ghcr.io/YOUR_USERNAME/REPO_NAME/telegram-bot:main`

### 3. Deploy to Server (`deploy.yml`)
**Triggers**:
- Automatically after successful Docker build
- Manual trigger

**What it does**:
- ✅ SSHs into your server
- ✅ Pulls latest code and Docker images
- ✅ Restarts services with `docker-compose`
- ✅ Verifies deployment via health checks
- ✅ Cleans up old Docker images

## Setup Instructions

### 1. Enable GitHub Container Registry
Your Docker images will be pushed to `ghcr.io` automatically. No additional setup needed!

### 2. Configure Deployment Secrets (Optional)
If you want auto-deployment to a server, add these secrets in **Settings → Secrets and variables → Actions**:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `DEPLOY_HOST` | Server IP or hostname | `123.45.67.89` |
| `DEPLOY_USER` | SSH username | `ubuntu` |
| `DEPLOY_SSH_KEY` | Private SSH key | `-----BEGIN RSA PRIVATE KEY-----...` |
| `DEPLOY_PORT` | SSH port (optional) | `22` |
| `DEPLOY_PATH` | Project path on server (optional) | `~/chatbot` |

### 3. Prepare Your Server (For Auto-Deploy)

On your deployment server:

```bash
# Install Docker and Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Clone your repository
git clone https://github.com/YOUR_USERNAME/REPO_NAME.git ~/chatbot
cd ~/chatbot

# Create .env file with your secrets
cp .env.example .env
nano .env  # Add your API keys

# Login to GitHub Container Registry
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u YOUR_USERNAME --password-stdin
```

### 4. Update docker-compose.yml for Production

Update image references in `docker-compose.yml`:

```yaml
services:
  chatbot-service:
    image: ghcr.io/YOUR_USERNAME/REPO_NAME/chatbot-service:main
    # ... rest of config

  telegram-bot:
    image: ghcr.io/YOUR_USERNAME/REPO_NAME/telegram-bot:main
    # ... rest of config
```

## Manual Deployment

If you prefer manual deployment:

```bash
# On your server
cd ~/chatbot
git pull origin main
docker-compose pull
docker-compose up -d
```

## Workflow Status Badges

Add these to your README.md:

```markdown
![CI](https://github.com/YOUR_USERNAME/REPO_NAME/actions/workflows/ci-build.yml/badge.svg)
![Docker](https://github.com/YOUR_USERNAME/REPO_NAME/actions/workflows/docker-build.yml/badge.svg)
![Deploy](https://github.com/YOUR_USERNAME/REPO_NAME/actions/workflows/deploy.yml/badge.svg)
```

## Troubleshooting

### Build fails with "package does not exist"
- Check that all dependencies are in `pom.xml`
- Ensure Java 21 is specified

### Docker push fails
- Verify `GITHUB_TOKEN` has package write permissions
- Check repository visibility settings

### Deployment fails
- Verify SSH key is correct (no passphrase)
- Check server firewall allows SSH
- Ensure Docker is installed on server
- Verify `.env` file exists on server

## Cost
All workflows use GitHub-hosted runners:
- ✅ **Free** for public repositories
- ✅ **2,000 minutes/month** for private repositories
