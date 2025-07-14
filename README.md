# 🤖 Evently: AI-Powered Event Management Telegram Bot (Java Edition)
<img width="1841" height="1046" alt="Screenshot 2025-05-31 223549" src="https://github.com/user-attachments/assets/4785cd06-274a-418a-afc6-03308b4adb20" />




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
![photo_2025-06-01_00-21-02](https://github.com/user-attachments/assets/3f4955df-8b05-49ba-a9f6-b9b28fb5366f)

![photo_2025-06-01_00-21-08](https://github.com/user-attachments/assets/307a1d52-26d8-4916-bc94-e558185fa918)
![photo_2025-06-01_00-21-13](https://github.com/user-attachments/assets/07987c10-fcb5-4ad9-ac08-bf7f2646d7a8)
![photo_2025-06-01_00-21-10](https://github.com/user-attachments/assets/5f804e7f-4b27-4ae0-87e8-4252711a1d7d)
![photo_2025-06-01_00-21-15](https://github.com/user-attachments/assets/32d129f5-4813-4ddc-8923-7017c8e86428)
![photo_2025-06-01_00-21-21](https://github.com/user-attachments/assets/a6a55c54-5a63-4b87-95d5-3c5f5935a2c9)

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
  <img src="https://i.pinimg.com/736x/b6/0d/e7/b60de7b5d546e6be01898eaabc01f79e.jpg" alt="Telegram Bot" width="60"/>
  <img src="https://tse3.mm.bing.net/th/id/OIP.ZPtdmwGk9OG8A1AFjWNIeQAAAA?rs=1&pid=ImgDetMain&o=7&rm=3" width="60"/>
  <img src="https://tse1.mm.bing.net/th/id/OIP.uksP1o57SCJJW91tgSWY7gHaHa?rs=1&pid=ImgDetMain&o=7&rm=3" alt="Google Calendar" width="60"/>
  <img src="https://cf.appdrag.com/dashboard-openvm-clo-b2d42c/uploads/supabase-TAiY.png" alt="Supabase" width="60"/>
  <img src="https://download.logo.wine/logo/Java_(programming_language)/Java_(programming_language)-Logo.wine.png" alt="Java" width="60"/>
</p>
