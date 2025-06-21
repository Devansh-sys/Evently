# 🤖 Evently: AI-Powered Event Management Telegram Bot (Java Edition)

![Evently Banner](assets/evently-banner.png)

Evently is a Java-based Telegram bot engineered for seamless, intelligent, and secure event management. Leveraging Google Gemini for natural language understanding, Google Calendar API for scheduling, and Supabase Postgres for persistent user authorization, Evently delivers a frictionless and robust experience for real users.

---

## 🚀 Features

- **Natural Language Event Extraction**  
  Forward or send event details as plain text—Evently uses the Google Gemini API to extract structured data (date, time, location, description) from unstructured messages.

- **Automated Google Calendar Integration**  
  Parsed events are instantly added to your Google Calendar using the Google Calendar API.

- **Persistent User Authorization with Supabase Postgres**  
  - Securely stores user authorization details (Telegram ID, OAuth tokens, session info).
  - One-time authorization: users don’t need to reauthorize on every interaction.
  - Utilizes Supabase Auth and Row Level Security (RLS) for data privacy.

- **Secure Credential Handling**  
  - All API keys and secrets are managed via a `.env` file (excluded from version control).
  - `.env.example` provided for easy setup.

- **Modular Java Architecture**  
  - Easily extensible for features like reminders, event updates, or calendar sharing.

---

## 🛠️ Technical Stack

| Component                  | Technology                                      |
|----------------------------|-------------------------------------------------|
| Core Application           | Java                                            |
| Telegram Bot Integration   | Telegram Bot Java Library                       |
| AI Event Extraction        | Google Gemini API                               |
| Calendar Integration       | Google Calendar API (Java Client)               |
| Persistent Storage/Auth    | Supabase Postgres + Supabase-Java Library       |
| Environment Management     | dotenv-java                                     |

---

## 🖼️ Screenshots

<p align="center">
  <img src="assets/evently-demo-1.png" alt="Evently Telegram Bot Screenshot" width="450"/>
  <img src="assets/evently-demo-2.png" alt="Evently Google Calendar Integration" width="450"/>
</p>

---

## 🔒 How Supabase Integration Works

1. **User Onboarding:**  
   - On first interaction, the bot authenticates the user and stores their authorization/session data in Supabase.
2. **Session Management:**  
   - For subsequent interactions, the bot retrieves credentials from Supabase, bypassing repeated authorization.
3. **Security:**  
   - All sensitive data is protected using Supabase’s Row Level Security and best practices for storing tokens and user data.

---

## ⚡ Getting Started

1. **Clone the repository**

git clone [https://github.com/Devansh-sys/Evently.git](https://github.com/Devansh-sys/Evently.git)
cd evently-telegram-bot


2. **Configure Environment Variables**
- Copy `.env.example` to `.env` and fill in your credentials.

3. **Install Dependencies**
- Use your preferred Java build tool (Maven/Gradle) to install dependencies.

4. **Run the Bot**
mvn exec:java
---

## 📦 Folder Structure

evently-telegram-bot/
│
├── src/
│ ├── main/
│ │ ├── java/
│ │ └── resources/
│ └── test/
├── assets/
│ ├── evently-banner.png
│ ├── evently-demo-1.png
│ └── evently-demo-2.png
├── .env.example
├── .gitignore
└── README.md

---

## 🧩 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---


## ✨ Evently delivers intelligent, secure, and persistent event management for Telegram users—powered by Java, Google Gemini & Calendar APIs, and Supabase Postgres. Effortlessly manage events: just forward details and let Evently handle scheduling, user authorization, and calendar integration with robust security and real-world usability.

---

<p align="center">
  <img src="assets/telegram-bot-icon.png" alt="Telegram Bot" width="60"/>
  <img src="assets/google-gemini-icon.png" alt="Google Gemini" width="60"/>
  <img src="assets/google-calendar-icon.png" alt="Google Calendar" width="60"/>
  <img src="assets/supabase-icon.png" alt="Supabase" width="60"/>
  <img src="assets/java-icon.png" alt="Java" width="60"/>
</p>
