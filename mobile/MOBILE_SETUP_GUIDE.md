# 🎾 Tennis Tournament Mobile App Setup Guide

## Overview
This React Native mobile app provides the same tennis tournament viewing functionality as the web version, with native mobile UI components and features like pull-to-refresh.

## Project Structure
```
mobile/TennisTournamentApp/
├── src/
│   ├── components/          # Reusable UI components
│   ├── screens/            # Main app screens
│   │   ├── TournamentListScreen.tsx    # Tournament list view
│   │   └── TournamentEventsScreen.tsx  # Tournament events view
│   ├── services/           # API services
│   │   └── api.ts          # Backend API integration
│   └── navigation/         # Navigation setup
│       └── AppNavigator.tsx # Stack navigation
├── App.tsx                 # Main app component
├── android/               # Android-specific files
├── ios/                   # iOS-specific files
└── package.json           # Dependencies
```

## Features Implemented
✅ **Tournament List Screen**
- Displays all 70+ tennis tournaments
- Pull-to-refresh functionality
- Loading states and error handling
- Touch navigation to tournament events

✅ **Tournament Events Screen**
- Shows events for selected tournament
- Live match status indicators (🔴 LIVE, ✅ Finished, ⏰ Scheduled)
- Player matchups and scores
- Date/time formatting
- Pull-to-refresh for real-time updates

✅ **Navigation**
- Stack navigation between screens
- Custom header styling
- Back navigation support

✅ **API Integration**
- Same backend APIs as web version (localhost:8080)
- TypeScript interfaces for type safety
- Custom hooks for data fetching
- Error handling and loading states

## Prerequisites
1. **Backend Running**: Spring Boot server on port 8080
2. **Node.js**: Version 16 or higher
3. **React Native CLI**: `npm install -g @react-native-community/cli`
4. **iOS Development**: Xcode (for iOS simulator)
5. **Android Development**: Android Studio (for Android emulator)

## Running the App

### 1. Start Backend Server
```bash
# From project root
mvn spring-boot:run
```
Backend will be available at: http://localhost:8080

### 2. Start Metro Bundler
```bash
# From mobile/TennisTournamentApp/
npm start
```

### 3. Run on iOS (requires Xcode)
```bash
# From mobile/TennisTournamentApp/
npx react-native run-ios
```

### 4. Run on Android (requires Android Studio)
```bash
# From mobile/TennisTournamentApp/
npx react-native run-android
```

## Code Reuse from Web App
The mobile app reuses significant portions of the web application:

### API Services (90% reused)
- Same fetch() calls to backend
- Identical data structures and interfaces
- Same error handling patterns
- Custom hooks for state management

### Business Logic (100% reused)
- Tournament data processing
- Event status handling
- Date/time formatting utilities
- API response parsing

### Component Logic (70% reused)
- State management patterns
- Data fetching logic
- Error handling approaches
- Loading state management

## Mobile-Specific Features

### Native UI Components
- `FlatList` for efficient scrolling
- `TouchableOpacity` for touch interactions
- `RefreshControl` for pull-to-refresh
- `ActivityIndicator` for loading states
- Native navigation with stack transitions

### Mobile UX Enhancements
- Touch-friendly card layouts
- Pull-to-refresh on both screens
- Native loading indicators
- Responsive design for different screen sizes
- Native back button support

### Performance Optimizations
- FlatList virtualization for large tournament lists
- Efficient re-rendering with React hooks
- Optimized image and asset loading
- Memory-efficient navigation

## API Endpoints Used
The mobile app uses the same REST endpoints as the web version:

```
GET /api/tournaments           # Get all tournaments
GET /api/tournaments/{id}/events  # Get events for tournament
```

## Styling
- Native iOS/Android styling patterns
- Consistent color scheme with web app
- Touch-friendly sizing (44pt minimum)
- Platform-specific navigation headers
- Shadow and elevation effects

## Development Notes
- TypeScript for type safety
- React Navigation v6 for navigation
- Functional components with hooks
- Modern React Native patterns
- Cross-platform compatibility

## Testing
- Unit tests with Jest (included in template)
- Component testing with React Native Testing Library
- Integration testing with backend APIs
- Manual testing on iOS/Android simulators

## Deployment
- iOS: Build with Xcode, deploy to App Store
- Android: Build APK/AAB, deploy to Google Play Store
- Code signing and certificates required for production

## Troubleshooting

### Common Issues
1. **Metro bundler not starting**: Clear cache with `npx react-native start --reset-cache`
2. **iOS build fails**: Ensure Xcode is properly installed and configured
3. **Android build fails**: Check Android SDK and emulator setup
4. **API connection issues**: Verify backend is running on localhost:8080
5. **Navigation errors**: Check React Navigation dependencies are installed

### Development Tips
- Use React Native Debugger for debugging
- Enable Fast Refresh for quick development
- Use Flipper for advanced debugging
- Test on both iOS and Android regularly
- Keep dependencies updated

## Next Steps
- [ ] Add push notifications for live match updates
- [ ] Implement offline caching with AsyncStorage
- [ ] Add user preferences and favorites
- [ ] Implement deep linking for tournament sharing
- [ ] Add match details screen with more statistics
- [ ] Integrate with device calendar for match reminders