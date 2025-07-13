# Fetch Rewards Coding Exercise - Software Engineering - Mobile

## Overview

This Android application retrieves and displays data from the Fetch Rewards API according to the specified requirements:

- Retrieves data from `https://hiring.fetch.com/hiring.json`
- Groups items by `listId` with collapsible sections
- Sorts first by `listId`, then alphabetically by `name`
- Filters out items with blank or null names
- Displays results in an easy-to-read list format

## Demo Video

A gif video demonstration of the app's functionality is included with this submission, showcasing the core features and user experience.

<div align="center">
  <img src="demo.gif" alt="Fetch App Demo" width="300">
</div>

## Technical Specifications

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 15)
- **Build Tools**: Android Gradle Plugin 8.11.1
- **Kotlin Version**: 2.2.0

## Setup and Installation

### Prerequisites
- **Android Studio**: Latest stable version
- **JDK**: Version 8 or higher 
- **Device/Emulator**: Android 7.0 (API 24) or higher

### Setup Steps

1. **Clone and Open**
   ```bash
   git clone [repository-url]
   cd FetchApp
   ```
   - Open project in Android Studio
   - Wait for Gradle sync to complete

2. **Build the Project**
   - Go to **Build → Clean Project**
   - Then **Build → Rebuild Project**
   - Ensure build completes successfully

3. **Run the Application**
   - Connect Android device (API 24+) with USB debugging, OR
   - Create AVD in Android Studio (Tools → AVD Manager)
   - Click green "Run" button or press Shift+F10

### Troubleshooting
- **Sync Issues**: File → Invalidate Caches and Restart
- **SDK Issues**: Tools → SDK Manager to install missing components

## Features

### Core Requirements
- API integration with data fetching
- Filtering out items with blank/null names
- Sorting by listId first, then alphabetically by name
- Visual grouping by listId

### Additional Features
- Collapsible groups (tap headers to expand/collapse)
- Pull-to-refresh functionality
- Error handling with retry options
- Loading states and progress indicators
- Fetch Rewards brand theming (purple/orange colors)

## Design Decisions

### Sorting Implementation
**Concern**: The requirement to "sort by name" could be interpreted as either alphabetical or numerical sorting. With the current data, this means "Item 280" appears before "Item 29" alphabetically.

**Decision**: I chose alphabetical sorting because:
- The requirement states "sort by name" without specifying numerical interpretation
- Alphabetical sorting is the standard default for string fields in most systems
- It provides consistent, predictable behavior

If numerical sorting was intended, it would typically be specified explicitly in the requirements.

### Architecture
 I chose MVVM (Model-View-ViewModel) because:
- Clear separation of concerns between UI logic (ViewModel) and presentation (Activity)
- ViewModel survives configuration changes (screen rotation)
- LiveData provides automatic UI updates when data changes
- Easy to test business logic in isolation from UI components
- Repository pattern fits naturally with MVVM for data operations

## Build Commands

```bash
./gradlew assembleDebug    # Debug build
./gradlew assembleRelease  # Release build
```
