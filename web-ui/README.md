# Web UI for Chatbot Debugging

A simple web interface to test and debug the chatbot service.

## Features

- 💬 Real-time chat interface
- 🔄 Thread reset functionality
- 👤 Custom user ID support
- 📊 Status indicators
- 🎨 Modern, responsive design

## Usage

### 1. Start the Chatbot Service

Make sure the chatbot service is running:

```bash
# Using Docker
docker-compose up chatbot-service

# Or locally
cd chatbot-service
mvn spring-boot:run
```

### 2. Open the Web UI

Simply open `index.html` in your web browser:

```bash
# Windows
start index.html

# Mac
open index.html

# Linux
xdg-open index.html
```

Or use a local web server:

```bash
# Python 3
python -m http.server 8000

# Then open http://localhost:8000
```

### 3. Start Chatting

1. Enter a User ID (default: `debug-user`)
2. Type your message in the input field
3. Press Enter or click Send
4. View the conversation and thread ID
5. Click "Reset Thread" to start a new conversation

## Configuration

Edit `script.js` to change the API endpoint:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Features

- **User ID Management**: Test different user threads
- **Thread Reset**: Clear conversation history
- **Status Indicators**: See connection and request status
- **Thread ID Display**: View the current OpenAI thread ID
- **Typing Indicators**: Visual feedback while waiting for responses
- **Error Handling**: Clear error messages if service is unavailable

## Troubleshooting

**CORS Errors**: The chatbot service has been configured to allow cross-origin requests. If you still see CORS errors, make sure the service is running and accessible.

**Connection Refused**: Ensure the chatbot service is running on port 8080.

**No Response**: Check the browser console (F12) for error messages.
