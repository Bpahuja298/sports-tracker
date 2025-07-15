# 📁 Tennis Tournament Viewer - Project Structure

## 🏗️ Repository Organization

```
sports-tracker/
├── 🌐 Web Application
│   ├── frontend/                    # React web app
│   │   ├── src/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   └── App.js
│   │   └── package.json
│   └── src/                        # Spring Boot backend
│       └── main/java/com/bha/sportsTracker/
│
├── 📱 Mobile Application
│   └── mobile/
│       └── TennisTournamentApp/    # React Native app
│           ├── src/
│           │   ├── components/
│           │   ├── screens/
│           │   ├── services/       # Shared API logic
│           │   └── navigation/
│           ├── android/
│           ├── ios/
│           └── package.json
│
├── 📚 Documentation
│   ├── README.md                   # Main project documentation
│   ├── iOS_APP_GUIDE.md           # Native iOS development guide
│   ├── REACT_NATIVE_GUIDE.md      # React Native implementation guide
│   ├── MOBILE_DEVELOPMENT_SUMMARY.md
│   └── PROJECT_STRUCTURE.md       # This file
│
└── 🔧 Configuration
    ├── .gitignore
    └── pom.xml                     # Maven configuration
```

## 🎯 Development Strategy

### Phase 1: Preserve Web Version ✅
- Keep existing `frontend/` directory intact
- Web app continues to work independently
- Same Spring Boot backend serves both web and mobile

### Phase 2: Create Mobile App 🚧
- New `mobile/TennisTournamentApp/` directory
- Reuse API services from web version
- Independent mobile development

### Phase 3: Shared Components (Future)
- Extract common API logic to shared utilities
- Potential monorepo structure with Lerna/Nx
- Shared TypeScript types

## 🔄 API Sharing Strategy

### Current Backend (Unchanged)
```
Spring Boot API (Port 8080)
├── /api/tournaments          # Used by both web & mobile
├── /api/tournaments/{id}/events  # Used by both web & mobile
└── CORS configured for both localhost:3000 and mobile
```

### Web Frontend (Preserved)
```
frontend/ (React Web App)
├── Port 3000
├── Existing components work as-is
└── No changes needed
```

### Mobile Frontend (New)
```
mobile/TennisTournamentApp/ (React Native)
├── Reuses same API endpoints
├── Similar component structure
└── Mobile-optimized UI
```

## 🚀 Development Workflow

### For Web Development:
```bash
# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Web Frontend
cd frontend
npm start
# Opens http://localhost:3000
```

### For Mobile Development:
```bash
# Terminal 1: Backend (same as web)
mvn spring-boot:run

# Terminal 2: React Native Metro
cd mobile/TennisTournamentApp
npx react-native start

# Terminal 3: iOS Simulator
npx react-native run-ios

# Terminal 4: Android Emulator
npx react-native run-android
```

## 📱 Mobile App Architecture

### Directory Structure
```
mobile/TennisTournamentApp/
├── src/
│   ├── components/              # Reusable UI components
│   │   ├── TournamentCard.js
│   │   ├── EventCard.js
│   │   └── EmptyState.js
│   ├── screens/                 # Screen components
│   │   ├── TournamentListScreen.js
│   │   ├── TournamentEventsScreen.js
│   │   └── EventDetailScreen.js
│   ├── services/                # API services (copied from web)
│   │   ├── api.js
│   │   └── tournamentService.js
│   ├── hooks/                   # Custom hooks
│   │   └── useTournaments.js
│   ├── navigation/              # React Navigation setup
│   │   └── AppNavigator.js
│   └── utils/                   # Utility functions
│       └── dateFormatter.js
├── android/                     # Android-specific files
├── ios/                         # iOS-specific files
├── App.js                       # Root component
└── package.json
```

### Code Reuse from Web App

#### API Services (90% Reusable)
```javascript
// Can copy directly from frontend/src/services/api.js
const API_BASE_URL = 'http://localhost:8080/api';

export const fetchTournaments = async () => {
  // Same logic as web version
};
```

#### Component Logic (70% Reusable)
```javascript
// Same hooks and state management
const [tournaments, setTournaments] = useState([]);
const [loading, setLoading] = useState(false);

useEffect(() => {
  fetchTournaments();
}, []);
```

#### Business Logic (100% Reusable)
```javascript
// Date formatting, data processing, etc.
const formatDateTime = (dateTimeString) => {
  // Exact same function as web version
};
```

## 🔧 Configuration Changes

### Backend CORS Update
```java
// Add mobile origins to CORS configuration
@CrossOrigin(origins = {
  "http://localhost:3000",      // Web app
  "http://localhost:8081",      // React Native Metro
  "http://10.0.2.2:8080"        // Android emulator
})
```

### Package.json Scripts
```json
{
  "scripts": {
    "web": "cd frontend && npm start",
    "mobile": "cd mobile/TennisTournamentApp && npx react-native start",
    "ios": "cd mobile/TennisTournamentApp && npx react-native run-ios",
    "android": "cd mobile/TennisTournamentApp && npx react-native run-android",
    "backend": "mvn spring-boot:run"
  }
}
```

## 🎯 Benefits of This Structure

### ✅ Advantages:
1. **Web app preserved**: Existing functionality untouched
2. **Independent development**: Mobile and web can evolve separately
3. **Code reuse**: Share API logic and business rules
4. **Single backend**: One API serves both platforms
5. **Easy maintenance**: Clear separation of concerns

### 🔄 Future Enhancements:
1. **Shared utilities**: Extract common code to shared package
2. **TypeScript**: Add type safety across both platforms
3. **Monorepo**: Use Lerna or Nx for better dependency management
4. **CI/CD**: Separate build pipelines for web and mobile

## 📚 Documentation Strategy

### For Developers:
- `README.md`: Overall project overview
- `REACT_NATIVE_GUIDE.md`: Mobile development guide
- `PROJECT_STRUCTURE.md`: This architecture document

### For Users:
- Web app: Existing documentation in README
- Mobile app: App store descriptions and user guides

This structure ensures both web and mobile versions can coexist and evolve independently while sharing the robust Spring Boot backend! 🚀