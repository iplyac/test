# OpenAI Chatbot with Telegram Integration

A production-ready chatbot system built with **Java 21** and **Spring Boot 3.2**, featuring OpenAI Assistants API with Threads for persistent conversations and Telegram bot integration.

## 🌟 Features

- **OpenAI Threads API**: One thread per user for persistent conversation history
- **Dynamic Persona**: Configurable bot personality via external file (auto-reloads every 60 seconds)
- **Telegram Integration**: Full-featured Telegram bot with command support
- **Spring AI**: Leverages Spring AI abstractions for OpenAI integration
- **Docker Deployment**: Complete containerization with docker-compose
- **Health Monitoring**: Built-in health checks and actuator endpoints
- **Java 21**: Modern Java with Virtual Threads for efficient async operations

## 🏗️ Architecture

```
┌─────────────────┐
│  Telegram User  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│   Telegram Bot Service  │
│   (Spring Boot 3.2)     │
│   Port: 8081            │
└────────┬────────────────┘
         │ REST API
         ▼
┌─────────────────────────┐      ┌──────────────┐
│  Chatbot Service        │◄─────┤ persona.txt  │
│  (Spring Boot 3.2)      │      │ (auto-reload)│
│  Port: 8080             │      └──────────────┘
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   OpenAI Assistants API │
│   (Threads + Messages)  │
└─────────────────────────┘
```

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **Docker** and **Docker Compose**
- **OpenAI API Key** ([Get one here](https://platform.openai.com/api-keys))
- **Telegram Bot Token** ([Create bot with @BotFather](https://t.me/botfather))

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd test
```

### 2. Configure Environment Variables

Copy the example environment file and add your API keys:

```bash
cp .env.example .env
```

Edit `.env` and add your credentials:

```env
OPENAI_API_KEY=your-openai-api-key-here
TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your-telegram-bot-token-here
```

### 3. Customize Bot Persona (Optional)

Edit `persona.txt` to define your bot's personality. The file is monitored and reloaded automatically every 60 seconds.

### 4. Build and Run with Docker

```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

### 5. Test the Services

**Chatbot Service Health Check:**
```bash
curl http://localhost:8080/api/health
```

**Send a Test Message:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"userId": "test-user", "message": "Hello!"}'
```

**Telegram Bot:**
Open Telegram and search for your bot username, then send `/start`

## 📦 Project Structure

```
.
├── chatbot-service/              # OpenAI chatbot service
│   ├── src/main/java/com/chatbot/
│   │   ├── ChatbotServiceApplication.java
│   │   ├── config/
│   │   │   └── OpenAIConfig.java
│   │   ├── controller/
│   │   │   └── ChatController.java
│   │   ├── model/
│   │   │   ├── ChatRequest.java
│   │   │   └── ChatResponse.java
│   │   └── service/
│   │       ├── OpenAIService.java
│   │       └── PersonaLoaderService.java
│   ├── Dockerfile
│   └── pom.xml
│
├── telegram-bot/                 # Telegram bot service
│   ├── src/main/java/com/telegram/
│   │   ├── TelegramBotApplication.java
│   │   ├── bot/
│   │   │   └── ChatBot.java
│   │   ├── model/
│   │   │   ├── ChatRequest.java
│   │   │   └── ChatResponse.java
│   │   └── service/
│   │       └── ChatbotClient.java
│   ├── Dockerfile
│   └── pom.xml
│
├── web-ui/                       # Web debug interface
│   ├── index.html
│   ├── styles.css
│   ├── script.js
│   └── README.md
│
├── docker-compose.yml            # Docker orchestration
├── pom.xml                       # Parent Maven POM
├── persona.txt                   # Bot personality configuration
├── .env                          # Environment variables (not in git)
├── .env.example                  # Environment template
└── README.md                     # This file
```

## 🔧 Configuration

### Chatbot Service (`chatbot-service/src/main/resources/application.properties`)

```properties
server.port=8080
openai.api-key=${OPENAI_API_KEY}
openai.model=gpt-4-turbo-preview
chatbot.persona.file-path=${PERSONA_FILE_PATH:./persona.txt}
```

### Telegram Bot (`telegram-bot/src/main/resources/application.properties`)

```properties
server.port=8081
telegram.bot.username=${TELEGRAM_BOT_USERNAME}
telegram.bot.token=${TELEGRAM_BOT_TOKEN}
chatbot.service.url=http://chatbot-service:8080
```

## 🤖 Telegram Bot Commands

- `/start` - Start the bot and see welcome message
- `/help` - Display available commands
- `/reset` - Reset conversation history (starts new thread)

## 🖥️ Web Debug UI

A simple web interface is included for testing and debugging:

1. Start the chatbot service: `docker-compose up chatbot-service`
2. Open `web-ui/index.html` in your browser
3. Enter a user ID and start chatting

Features:
- Real-time chat interface
- Thread management
- Status indicators
- Custom user ID support

See [web-ui/README.md](web-ui/README.md) for details.

## 🛠️ Development

### Build Locally

```bash
# Build all modules
mvn clean package

# Build specific module
mvn clean package -pl chatbot-service
mvn clean package -pl telegram-bot
```

### Run Locally (without Docker)

**Terminal 1 - Chatbot Service:**
```bash
cd chatbot-service
mvn spring-boot:run
```

**Terminal 2 - Telegram Bot:**
```bash
cd telegram-bot
mvn spring-boot:run
```

Make sure to set environment variables before running locally.

## 🔍 API Endpoints

### Chatbot Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat` | Send message to chatbot |
| DELETE | `/api/thread/{userId}` | Reset user's conversation thread |
| GET | `/api/health` | Health check |
| GET | `/actuator/health` | Detailed health status |

### Example Request

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "message": "What is the weather like?"
  }'
```

### Example Response

```json
{
  "response": "I don't have access to real-time weather data...",
  "threadId": "thread_abc123xyz"
}
```

## 🐳 Docker Commands

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and restart
docker-compose up --build -d

# View service status
docker-compose ps
```

## 📊 Monitoring

Both services expose Spring Boot Actuator endpoints:

- Chatbot Service: http://localhost:8080/actuator/health
- Telegram Bot: http://localhost:8081/actuator/health

## 🔐 Security Notes

- **Never commit `.env` file** - It contains sensitive API keys
- API keys are passed as environment variables to Docker containers
- The `.gitignore` file excludes `.env` by default
- For production, use secrets management (e.g., Docker Secrets, Kubernetes Secrets)

## 🧪 Testing

### Test Persona Reload

1. Start the services
2. Send a message via Telegram
3. Edit `persona.txt` with a different personality
4. Wait 60 seconds
5. Start a new conversation (use `/reset`)
6. Notice the changed behavior

### Test Thread Persistence

1. Send messages to the bot
2. Restart the services: `docker-compose restart`
3. Continue the conversation - context is preserved

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [OpenAI](https://openai.com/) for the Assistants API
- [Spring AI](https://spring.io/projects/spring-ai) for AI integration abstractions
- [TelegramBots](https://github.com/rubenlagus/TelegramBots) for Java Telegram Bot API

## 📧 Support

For issues and questions:
- Open an issue on GitHub
- Check existing issues for solutions

---

**Built with ❤️ using Java 21, Spring Boot 3.2, and OpenAI**
