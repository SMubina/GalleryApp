# 📸 Gallery App – Android Media Viewer

A modern and responsive **Gallery App** built using **Kotlin**, **Coroutines**, and **MVVM**, showcasing all your **images and videos** in organized albums. The app dynamically loads content from your device and groups it into folders like "Camera", "Screenshots", "All Images", and "All Videos".



## ✨ Features

- 🖼 Display both **Images & Videos** in a unified album view
- 📂 Group media files by folder name (e.g., Camera, Screenshots, WhatsApp)
- 🧭 Navigate to detail screen with media preview from each album
- 📷 Special albums:
  - **All Images**
  - **All Videos**
  - **Camera**
- 🔁 Auto-refreshes media content using `ContentObserver` and `Flow`
- 💨 Asynchronous loading with Kotlin **Coroutines**
- 🧹 Filters out thumbnails, cache, and `.nomedia` files
- 📏 Shows media count in each album
- 🔍 Detail screen with full media grid per album
- ✅ Clean & maintainable **MVVM architecture**




## 🛠 Tech Stack



| Layer         | Tech |
|---------------|------|
| Language      | Kotlin |
| Architecture  | MVVM, Repository Pattern |
| UI            | RecyclerView, ViewBinding |
| Async         | Kotlin Coroutines, Flows |
| Media Access  | `MediaStore`, `ContentResolver`, `ContentObserver` |
| Image Loader  | Coil |
| Lifecycle     | ViewModel, Lifecycle-aware components |
| Navigation    | Fragment Navigation




## 📦 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/SMubina/GalleryApp.git
2. Open the project in Android Studio (Giraffe or newer)
3. Connect a physical device(recommended) or use an emulator with media (API 30+)
4. Run the app
5. Grant storage/media access permission on launch
6. Once permission granted media album will be loaded


