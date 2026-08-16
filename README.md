# PensioniIm 📱

An Android identity verification application built with Kotlin and Jetpack Compose, designed to guide users through a secure registration and identity-verification process.

The application combines a modern Compose UI with camera-based verification and AWS services to provide a guided onboarding experience.

## ✨ Overview

PensioniIm is an Android application focused on user onboarding and identity verification.

The application guides the user through several steps:

1. Enter and verify a phone number
2. Confirm the user's identity
3. Capture a selfie using the device camera
4. Perform a liveness verification
5. Display the verification result

The goal is to make the verification process simple and intuitive while providing the technical foundation for secure identity verification.

## 🚀 Features

- 📱 Phone number registration
- 🔐 Phone number verification
- 👤 Identity verification flow
- 🤳 Camera-based selfie capture
- 🧍 Liveness detection
- ✅ Verification success/failure handling
- ☁️ AWS-powered authentication and API integration
- 🎨 Modern UI built with Jetpack Compose
- 📷 CameraX integration
- 🔄 Guided multi-step onboarding experience

## 📱 Application Flow

### 1. Start Verification

The user is introduced to the verification process and can start the onboarding flow.

### 2. Phone Number Verification

The user enters their mobile phone number.

The application then validates the entered number before continuing.

### 3. Identity Verification

The user is asked to confirm their identity and continue with the verification process.

### 4. Selfie & Liveness Check

The application opens the device camera and guides the user through positioning their face correctly.

The liveness flow verifies that the captured subject is a real person rather than a static image.

### 5. Verification Result

After the verification process is completed, the application displays a success or failure state.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Declarative UI |
| Android SDK 34 | Application target |
| CameraX | Camera and image capture |
| AWS Amplify | Cloud integration |
| Amazon Cognito | Authentication |
| AWS API | Backend/API communication |
| Amplify Liveness | Face/liveness verification |
| Navigation Compose | Screen navigation |
| Coil | Image loading |
| OkHttp | HTTP networking |

## 🏗️ Architecture & Project Structure

The application is organized as a native Android application using Kotlin and Jetpack Compose.

The project uses:

- Compose-based UI components
- Android lifecycle-aware components
- Navigation Compose for screen transitions
- AWS Amplify for cloud services
- CameraX for camera functionality
- Dedicated application initialization for AWS services

AWS Amplify is initialized at application startup with the AWS API and Cognito authentication plugins.

## ☁️ AWS Integration

The application uses AWS services as part of the authentication and verification workflow.

AWS Amplify is configured with:

- AWS API
- Amazon Cognito Authentication
- Amplify Core
- Amplify Liveness

This allows the Android application to communicate with cloud services while keeping the mobile application focused on the user experience.

## 🔐 Identity Verification

One of the key features of the application is the identity verification flow.

The user is guided through a camera-based process where they must:

1. Position their face inside the provided frame
2. Move closer/further from the camera when requested
3. Hold their face in the required position
4. Complete the liveness verification

The application then reports whether the verification was successful.

## 📸 Screenshots

### Registration & Verification

<p align="center">
  <img src="screenshots/registration.png" width="220">
  <img src="screenshots/phone-verification.png" width="220">
  <img src="screenshots/identity-verification.png" width="220">
</p>

### Liveness Verification

<p align="center">
  <img src="screenshots/face-position.png" width="220">
  <img src="screenshots/move-closer.png" width="220">
  <img src="screenshots/hold-still.png" width="220">
</p>

### Verification Result

<p align="center">
  <img src="screenshots/success.png" width="220">
  <img src="screenshots/failure.png" width="220">
</p>

## 🧩 Key Technical Highlights

### Jetpack Compose

The user interface is implemented using Jetpack Compose, allowing the application to build reusable and reactive UI components.

### CameraX

CameraX is used to provide camera functionality required for the selfie and identity-verification workflow.

### AWS Cognito

Amazon Cognito is used as part of the application's authentication infrastructure.

### AWS Amplify

Amplify provides the integration layer between the Android client and AWS services.

### Liveness Verification

The application integrates a liveness verification flow to help distinguish a live user from a static image or other presentation attack.

## 🔧 Requirements

- Android Studio
- JDK 8+
- Android SDK 34
- Android device or emulator
- Camera access
- AWS configuration for authentication/API services

## ▶️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Alberiana/pensioniImApp.git
cd pensioniImApp
