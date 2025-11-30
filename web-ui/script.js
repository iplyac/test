// Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// DOM Elements
const chatContainer = document.getElementById('chatContainer');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');
const resetBtn = document.getElementById('resetBtn');
const userIdInput = document.getElementById('userId');
const statusEl = document.getElementById('status');
const threadIdEl = document.getElementById('threadId');

// State
let currentThreadId = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    sendBtn.addEventListener('click', sendMessage);
    resetBtn.addEventListener('click', resetThread);

    // Clear welcome message on first interaction
    messageInput.addEventListener('focus', () => {
        const welcome = chatContainer.querySelector('.welcome-message');
        if (welcome) {
            welcome.remove();
        }
    }, { once: true });

    updateStatus('Ready', 'connected');
});

// Send message to chatbot
async function sendMessage() {
    const message = messageInput.value.trim();
    const userId = userIdInput.value.trim();

    if (!message || !userId) {
        return;
    }

    // Clear input
    messageInput.value = '';

    // Add user message to chat
    addMessage('user', message);

    // Show typing indicator
    const typingId = showTypingIndicator();

    // Disable send button
    sendBtn.disabled = true;
    updateStatus('Sending...', 'sending');

    try {
        const response = await fetch(`${API_BASE_URL}/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                message: message
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        // Remove typing indicator
        removeTypingIndicator(typingId);

        // Add assistant response
        addMessage('assistant', data.response);

        // Update thread ID
        if (data.threadId) {
            currentThreadId = data.threadId;
            threadIdEl.textContent = `Thread: ${data.threadId}`;
        }

        updateStatus('Ready', 'connected');
    } catch (error) {
        console.error('Error sending message:', error);
        removeTypingIndicator(typingId);
        addMessage('assistant', `Error: ${error.message}. Make sure the chatbot service is running on ${API_BASE_URL}`);
        updateStatus('Error', 'error');
    } finally {
        sendBtn.disabled = false;
        messageInput.focus();
    }
}

// Reset conversation thread
async function resetThread() {
    const userId = userIdInput.value.trim();

    if (!userId) {
        alert('Please enter a User ID');
        return;
    }

    if (!confirm('Are you sure you want to reset the conversation?')) {
        return;
    }

    updateStatus('Resetting...', 'sending');

    try {
        const response = await fetch(`${API_BASE_URL}/thread/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Clear chat
        chatContainer.innerHTML = '<div class="welcome-message"><p>🔄 Thread reset</p><p>Start a new conversation</p></div>';
        currentThreadId = null;
        threadIdEl.textContent = '';

        updateStatus('Ready', 'connected');
    } catch (error) {
        console.error('Error resetting thread:', error);
        updateStatus('Error', 'error');
        alert(`Failed to reset thread: ${error.message}`);
    }
}

// Add message to chat UI
function addMessage(role, content) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${role}`;

    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    contentDiv.textContent = content;

    const timeDiv = document.createElement('div');
    timeDiv.className = 'message-time';
    timeDiv.textContent = new Date().toLocaleTimeString();

    contentDiv.appendChild(timeDiv);
    messageDiv.appendChild(contentDiv);
    chatContainer.appendChild(messageDiv);

    // Scroll to bottom
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

// Show typing indicator
function showTypingIndicator() {
    const typingDiv = document.createElement('div');
    typingDiv.className = 'message assistant';
    typingDiv.id = 'typing-indicator';

    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';

    const indicatorDiv = document.createElement('div');
    indicatorDiv.className = 'typing-indicator';
    indicatorDiv.innerHTML = '<span></span><span></span><span></span>';

    contentDiv.appendChild(indicatorDiv);
    typingDiv.appendChild(contentDiv);
    chatContainer.appendChild(typingDiv);

    chatContainer.scrollTop = chatContainer.scrollHeight;

    return 'typing-indicator';
}

// Remove typing indicator
function removeTypingIndicator(id) {
    const indicator = document.getElementById(id);
    if (indicator) {
        indicator.remove();
    }
}

// Update status
function updateStatus(text, className) {
    statusEl.textContent = text;
    statusEl.className = className;
}
