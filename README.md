# Messenger Application

A client-server instant messaging application built with Java, featuring a JavaFX-based desktop client and a CLI-based server with secure SSL/TLS communication.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Data Models](#data-models)
- [Communication Protocol](#communication-protocol)
- [Database Schema](#database-schema)
- [Screenshots](#screenshots)

## Overview

This is a real-time messaging application consisting of two separate modules:

1. **MessengerServer** - A multi-threaded server application that handles client connections, user authentication, message routing, and data persistence using MySQL.

2. **MessengerClient** - A JavaFX-based desktop client application providing a graphical user interface for users to register, login, and exchange messages with other users.

## Features

### User Management
- User registration with username, full name, and password
- User authentication (login/logout)
- Online user status tracking
- Automatic login after registration

### Messaging
- **Private Conversations**: One-on-one messaging between two users
- **Group Conversations**: Multi-user group chats with shared membership
- Real-time message delivery
- File attachments support in messages
- Message history persistence

### Security
- SSL/TLS encrypted socket communication
- Password-protected user accounts
- Server-side input validation

### UI Features (Client)
- Modern JavaFX-based user interface
- Custom CSS styling with Lato font
- Dialog-based login and registration
- Real-time online user list
- Conversation list with chat heads
- Message bubbles for sent/received messages
- File attachment support

## Architecture

```
┌─────────────────────┐         SSL/TLS          ┌─────────────────────┐
│   MessengerClient   │◄─────────────────────────►│   MessengerServer   │
│   (JavaFX GUI)      │        TCP Socket         │   (CLI Server)      │
└─────────────────────┘         Port 60000        └─────────────────────┘
                                                              │
                                                              │ Hibernate
                                                              ▼
                                                      ┌───────────────┐
                                                      │  MySQL DB     │
                                                      │  (Docker)     │
                                                      └───────────────┘
```

### Server Architecture
- **Multi-threaded**: Each client connection is handled in a separate thread ([`UserSessionThread`](MessengerServer/src/main/java/hanu/npr/messengerserver/controllers/UserSessionThread.java))
- **Event-driven callbacks**: Server events are communicated through the [`ServerController.Interface`](MessengerServer/src/main/java/hanu/npr/messengerserver/controllers/ServerController.java:327)
- **Repository Pattern**: Data access is abstracted through repository classes

### Client Architecture
- **MVC Pattern**: Separates UI (FXML), logic (Controller), and data (Models)
- **Observer Pattern**: Callback interface for handling server responses
- **DTO Pattern**: Data Transfer Objects for serialization

## Technologies Used

### Server
| Technology | Purpose |
|------------|---------|
| Java 8 | Core language |
| Hibernate 5.6 | ORM for database operations |
| MySQL 8 | Relational database |
| Jackson | JSON serialization/deserialization |
| Apache Tika | File type detection |
| SSL/TLS | Secure socket communication |
| Lombok | Boilerplate reduction |
| Docker | Database containerization |

### Client
| Technology | Purpose |
|------------|---------|
| JavaFX 17 | GUI framework |
| Jackson | JSON serialization/deserialization |
| JFoenix | Material Design components |
| ControlsFX | Additional JavaFX controls |
| Apache Commons IO | File utilities |
| Lombok | Boilerplate reduction |

## Project Structure

```
.
├── MessengerClient/
│   ├── pom.xml                          # Maven configuration
│   └── src/main/
│       ├── java/hanu/npr/messengerclient/
│       │   ├── MessengerClientApplication.java    # Entry point
│       │   ├── controllers/
│       │   │   └── MainController.java            # Main client logic
│       │   ├── models/                            # Data models
│       │   │   ├── User.java
│       │   │   ├── Conversation.java
│       │   │   ├── PrivateConversation.java
│       │   │   ├── GroupConversation.java
│       │   │   ├── ChatMessage.java
│       │   │   ├── Attachment.java
│       │   │   └── dtos/                          # Data Transfer Objects
│       │   ├── fxComponents/                      # Custom JavaFX components
│       │   │   ├── ChatHead.java
│       │   │   ├── LoginDialog.java
│       │   │   ├── SignUpDialog.java
│       │   │   ├── MessageCell.java
│       │   │   └── ...
│       │   ├── fxViews/
│       │   │   └── Main.java                      # Main view launcher
│       │   ├── exceptions/                        # Custom exceptions
│       │   └── utils/
│       │       └── MyFileUtils.java
│       └── resources/
│           ├── hanu/npr/messengerclient/
│           │   ├── fxViews/main.fxml              # Main UI layout
│           │   ├── fxComponents/                  # Component FXML files
│           │   ├── css/main.css                   # Stylesheet
│           │   ├── fonts/Lato-Regular.ttf
│           │   └── images/                        # Icons and images
│           └── hibernate.cfg.xml                  # Hibernate config
│
├── MessengerServer/
│   ├── pom.xml                          # Maven configuration
│   ├── docker-compose.yml               # MySQL Docker configuration
│   └── src/main/
│       ├── java/hanu/npr/messengerserver/
│       │   ├── MessengerServerCLIApplication.java # Entry point
│       │   ├── controllers/
│       │   │   ├── ServerController.java          # Main server logic
│       │   │   └── UserSessionThread.java         # Client session handler
│       │   ├── models/                            # Data models (same as client)
│       │   ├── repositories/                      # Data access layer
│       │   │   ├── CRUDRepository.java
│       │   │   ├── UserRepository.java
│       │   │   ├── ConversationRepository.java
│       │   │   ├── PrivateConversationRepository.java
│       │   │   ├── GroupConversationRepository.java
│       │   │   ├── ChatMessageRepository.java
│       │   │   └── AttachmentRepository.java
│       │   ├── config/
│       │   │   ├── Db.java                        # Database config
│       │   │   └── InitData.java                  # Initial data setup
│       │   ├── exceptions/                        # Custom exceptions
│       │   └── utils/
│       │       └── MyFileUtils.java
│       └── resources/
│           └── hanu/npr/messengerserver/
│               └── hibernate.cfg.xml              # Hibernate configuration
│
└── .gitignore
```

## Prerequisites

- **Java Development Kit (JDK) 8** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for database)
- **MySQL 8** (alternative to Docker)

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd messenger-application
```

### 2. Start the Database

Using Docker Compose (recommended):
```bash
cd MessengerServer
docker-compose up -d
```

Or use an existing MySQL 8 server and create a database named `messenger`.

### 3. Build the Server

```bash
cd MessengerServer
mvn clean package
```

### 4. Build the Client

```bash
cd MessengerClient
mvn clean package
```

## Configuration

### Server Configuration

Edit [`hibernate.cfg.xml`](MessengerServer/src/main/resources/hanu/npr/messengerserver/hibernate.cfg.xml) for database settings:

```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost/messenger
</property>
<property name="hibernate.connection.username">
    root
</property>
<property name="hibernate.connection.password">
    MessengerOnTCP
</property>
```

The server port is configured in [`MessengerServerCLIApplication.java`](MessengerServer/src/main/java/hanu/npr/messengerserver/MessengerServerCLIApplication.java:11):
```java
new ServerController(60000, this);
```

### Docker Configuration

Edit [`docker-compose.yml`](MessengerServer/docker-compose.yml) to modify MySQL settings:
```yaml
environment:
  MYSQL_ROOT_PASSWORD: MessengerOnTCP
  MYSQL_DATABASE: messenger
```

## Usage

### Starting the Server

```bash
cd MessengerServer
java -jar target/MessengerServer-1.0.0.jar
```

Expected output:
```
60000 has been opened
```

### Starting the Client

```bash
cd MessengerClient
java -jar target/MessengerClient-1.0.0.jar
```

### Using the Application

1. **Register**: Click "Sign Up" and enter your full name, username, and password
2. **Login**: Enter your username and password
3. **Start Chatting**: 
   - Select an online user to start a private conversation
   - Join group conversations
   - Send messages with optional file attachments

## Data Models

### User
```java
- username: String (unique identifier)
- password: String
- fullName: String
- groupConversations: Collection<GroupConversation>
```

### Conversation (Base class)
```java
- id: Long
- chatMessages: Collection<ChatMessage>
- latestMessage: ChatMessage
```

### PrivateConversation (extends Conversation)
```java
- user1: User
- user2: User
```

### GroupConversation (extends Conversation)
```java
- name: String
- members: Collection<User>
```

### ChatMessage
```java
- id: Long
- conversation: Conversation
- sender: User
- content: String
- attachment: Attachment
```

### Attachment
```java
- id: Long
- fileName: String
- fileType: String
- file: byte[]
```

## Communication Protocol

The client and server communicate using JSON-based DTOs (Data Transfer Objects). Each message includes a `type` field for message identification.

### Message Types

| Type | Direction | Description |
|------|-----------|-------------|
| `registration` | Client → Server | User registration data |
| `login` | Client → Server | User login credentials |
| `loggedInUser` | Server → Client | Logged-in user information |
| `initialData` | Server → Client | Online users and conversations |
| `chatMessage` | Bidirectional | Chat message with optional attachment |
| `privateConversation` | Bidirectional | Private conversation data |
| `newUserJoined` | Server → Client | Notification of new online user |
| `downloadAttachmentRequest` | Client → Server | Request to download attachment |
| `downloadAttachmentResponse` | Server → Client | Attachment data |
| `error` | Server → Client | Error message |
| `success` | Server → Client | Success confirmation |

### Example JSON Payloads

**Login Request:**
```json
{
  "type": "login",
  "username": "john_doe",
  "password": "secret123"
}
```

**Chat Message:**
```json
{
  "type": "chatMessage",
  "conversationId": 1,
  "senderUsername": "john_doe",
  "content": "Hello, World!",
  "attachment": null
}
```

## Database Schema

The database tables are auto-generated by Hibernate based on the entity classes:

- **User** - User accounts
- **Conversation** - Base conversation table
- **PrivateConversation** - Private chats (joined with Conversation)
- **GroupConversation** - Group chats (joined with Conversation)
- **ChatMessage** - All messages
- **Attachment** - File attachments
- **Participation** - Many-to-many relationship between users and group conversations

## Screenshots

The client features:
- Login dialog with username/password fields
- Registration dialog with full name, username, and password
- Main chat interface with:
  - Online users list on the left
  - Conversation list with chat heads
  - Message area with sent/received message bubbles
  - File attachment button
  - Message input field

## License

This project is developed for educational purposes at Hanoi University (HANU).

## Authors

HANU NPR Team
