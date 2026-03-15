# Filmadaran — Cinematic TV Explorer

Filmadaran is a production-ready Android application built with a high-performance **Multi-Module** and **Clean Architecture** stack. I built this not just to meet basic requirements, but to showcase a design system that scales for real-world enterprise growth.

## 🏗 Why this Architecture?

When I started this project, I made several deliberate technical choices to ensure it remains clean even if the codebase grows tenfold.

### 1. Multi-Module (Built for the Future)
Instead of a simple "app" folder, I split the project into `:domain`, `:presentation`, `:data`, and `:core`. 
*   **Scalability**: In large teams, modularity is essential. This setup allows different developers to work on features simultaneously without constant merge conflicts.
*   **Future Growth**: If we decide to add extra features like User Profiles or a TV Guide in the future, we can simply create new modules like `:feature:profile`. This keeps the build times fast because Gradle only recompiles what changed.
*   **Strict Isolation**: The compilation layer ensures that my UI code can't "cheat" and access the database directly. Everything must go through the Domain layer.

### 2. Clean Architecture + Use Cases
I implemented a dedicated **Interactors (Use Cases)** layer. ViewModels no longer talk to repositories directly; they use single-purpose classes like `GetShowsUseCase.kt`.
*   **Rationale**: This makes the business logic extremely easy to test and reuse. If the detail screen ever needs to toggle favorites exactly like the list screen does, we use the same `ToggleFavoriteUseCase`, avoiding code duplication.

### 3. Why Koin instead of Dagger/Hilt?
I chose **Koin** as the DI framework because of its weight and developer experience.
*   **Performance**: Dagger depends on massive annotation processing (KAPT/KSP) which slows down build times significantly in large projects. Koin is a pure Kotlin DSL—it’s fast, reflection-less, and incredibly readable.
*   **Simplicity**: Using `viewModelOf` and `singleOf` helps me avoid the "Boilerplate Hell" that usually comes with Hilt, while still providing the same level of decoupled dependencies.

### 4. Data Strategy
*   **Offline First**: I used **Room** for favorites instead of in-memory caching. A real app should always preserve user data between restarts.
*   **ID-based Detail Fetching**: When navigating, I only pass an ID and fetch details freshly. This ensures the data is never "stale" and makes the app compatible with Deep Links.

## 📦 Tech Stack
*   **Kotlin** & **MVVM**
*   **Retrofit** & **Moshi/Gson** for API
*   **Room Database** for local persistence
*   **Flow & Coroutines** for reactive data
*   **Material 3** with Edge-to-Edge support

