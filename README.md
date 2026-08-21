# 🎙️ AI Meeting Summarizer

A full-stack enterprise-grade web application built with **Java Spring Boot** that automatically transcribes meeting audio files and uses Generative AI to extract structured insights, including an Executive Summary, Key Decisions, and Action Items.

---

## 📺 Project Demo


https://github.com/user-attachments/assets/a34707aa-974c-4c2e-b9da-657a01cb5270



---

## ✨ Features

* **Large Audio File Support:** Safely upload meeting recordings (MP3, WAV, M4A) up to 100MB without crashing.
* **High-Accuracy Transcription:** Utilizes **AssemblyAI** to transcribe long-form audio seamlessly, bypassing standard 25MB API limits.
* **Lightning-Fast AI Summarization:** Integrates with **Groq's OpenAI-compatible API** (running open-source LLMs) for instant, structured meeting analysis without rate-limit bottlenecks.
* **Smart Dashboard:** A beautifully styled, responsive UI that saves your meeting history locally so you never lose past summaries.
* **One-Click Export:** Easily copy the generated Executive Summary, Key Decisions, and Action Items to your clipboard for sharing via email or Slack.

---

## 🏗️ Architecture & Pipeline

```text
[ Browser / Client ]
       │  (Multipart Audio Upload)
       ▼
[ Spring Boot Controller ] ──> [ TranscriptionService ] ──> AssemblyAI API
       │                                                          │
       │                                                  (Raw Transcript)
       │                                                          ▼
       └─────────────────────> [ SummaryService ]       ──> Groq
                                       │                          │
                               (JSON / Markdown) ◄────────────────┘
                                       ▼
                             [ Rendered UI Output ]
```

## 🛠️ Tech Stack

* **Backend:** Java 17+, Spring Boot, Maven
* **Frontend:** HTML5, CSS3 (Custom Light-Orange Theme), Vanilla JavaScript
* **Audio Processing:** AssemblyAI API
* **Large Language Model (LLM):** Groq API (`openai/gpt-oss-20b` or Llama 3 series)

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Maven installed
* Free API keys from [AssemblyAI](https://www.assemblyai.com/) and [Groq](https://console.groq.com/)

## Setup & Installation
### 1. Clone the Repository
Run the following commands in your terminal:
```bash
git clone [https://github.com/Anshu-priya-23/Meeting-summarizer.git](https://github.com/Anshu-priya-23/Meeting-summarizer.git)
cd Meeting-summarizer
```
### 2. Configure API Keys & Environment
Set your API keys in your environment or update src/main/resources/application.properties:
```
Properties
# Server Port
server.port=8080

# API Keys
assemblyai.api.key=your_assemblyai_api_key_here
groq.api.key=your_groq_api_key_here

# Override Spring Boot's default 1MB file size limit to allow full meeting uploads
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```
### 3. Build and Run
```
Bash
# On Linux / macOS:
./mvnw clean spring-boot:run

# On Windows:
./mvnw.cmd clean spring-boot:run
Once running, navigate to http://localhost:8080 in your web browser.
```
## 🔌 API Reference

Upload & Summarize Audio
* Endpoint: POST /api/upload (or your configured controller route)

* Content-Type: multipart/form-data

* Body: file (Binary audio file)
```
Sample JSON Response:
JSON
{
  "status": "success",
  "transcript": "Good morning everyone. Today we agreed on the Q4 release...",
  "summary": "### Executive Summary\nThe team discussed key release goals...\n\n### Action Items\n* Prepare rollout plan"
}
```
## 📁 Project Structure
Plaintext
```
meeting-summarizer/
├── src/
│   ├── main/
│   │   ├── java/com/example/meetingsummarizer/
│   │   │   ├── controller/
│   │   │   │   └── AudioUploadController.java
│   │   │   ├── service/
│   │   │   │   ├── TranscriptionService.java
│   │   │   │   └── SummaryService.java
│   │   │   └── MeetingsummarizerApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   └── index.html
│   │       └── application.properties
├── assets/
│   └── meeting-summarizer-video.mp4
├── pom.xml
└── README.md
<<<<<<< Updated upstream
```
## 📄 License

This project is open source and available under the [MIT License](https://choosealicense.com/licenses/mit/).

