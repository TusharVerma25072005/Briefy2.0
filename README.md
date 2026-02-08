# Briefy Android App

**Briefy** is a mobile application that helps users receive **summarized and categorized emails**. It integrates with Gmail via OAuth 2.0, extracts important information, and provides concise summaries while keeping sensitive data private.

This Android app is written in **Kotlin** and uses **Jetpack Compose** for UI, along with background processing and networking to interact with the Briefy backend.


Briefy is a smart email summarization app.
- [Briefy Backend](https://github.com/TusharVerma25072005/Briefy2.0-Backend) – handles user authentication, email storage, and processing.
- [Briefy AI Processing](https://github.com/Tanishq1706P/Andorid_Email_Project) – AI workers for summarization and categorization of emails.


---

## **Features**

1. **User Authentication**
    - Login with **Google Sign-In**.
    - Requests permissions to read emails.
    - Sends **serverAuthCode** to backend for exchanging refresh & access tokens.

2. **Email Fetching & Masking**
    - Fetches user emails via Gmail API (through backend or directly using tokens).
    - Masks sensitive information (names, addresses, numbers) before sending to the backend.

3. **Email Summarization**
    - Sends masked emails to the backend for AI-based summarization.
    - Receives summaries and categorized email data from the backend.

4. **Notifications**
    - Notifies users when email summaries are ready.
    - Can display important or high-priority email alerts.

5. **Secure & Private**
    - Sensitive data is **masked on the device** before sending.
    - Only masked emails are sent to the server.
    - Backend handles encryption and storage securely.

---
