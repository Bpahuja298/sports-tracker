# 🔧 React Native Build Troubleshooting Guide

## 🚨 Common Build Issues & Solutions

### **Issue 1: Library 'DoubleConversion' not found**
**Symptoms:** 
- Xcode build fails with library not found errors
- Linker command failed with exit code 1

**Solution (Currently Running):**
```bash
cd mobile/TennisTournamentApp/ios
rm -rf Pods Podfile.lock
pod install
```

### **Issue 2: Signing for 'TennisTournamentApp' requires a development team**
**Solution:**
1. In Xcode → Signing & Capabilities tab
2. Click "Add Account..." next to Team
3. Sign in with Apple ID (free account works)
4. Select your team from dropdown

### **Issue 3: No iOS devices or simulators found**
**Solution:**
1. Open Xcode → Settings → Platforms
2. Install iOS platform if not already installed
3. Or use: `xcrun simctl list devices` to check available simulators

## 🎯 Step-by-Step Build Process

### **Method 1: Using React Native CLI (Recommended)**
```bash
# Ensure Metro bundler is running
cd mobile/TennisTournamentApp
npm start

# In another terminal, run iOS
npx react-native run-ios --simulator="iPhone 16 Pro"
```

### **Method 2: Using Xcode**
1. Open `mobile/TennisTournamentApp/ios/TennisTournamentApp.xcworkspace`
2. Select simulator or device
3. Fix signing if needed
4. Click ▶️ Play button

### **Method 3: Using Yarn (Alternative)**
```bash
cd mobile/TennisTournamentApp
yarn ios
```

## 🔄 Clean Build Process

If you encounter persistent issues:

```bash
# Clean React Native cache
cd mobile/TennisTournamentApp
npx react-native start --reset-cache

# Clean iOS build
cd ios
rm -rf build
rm -rf Pods Podfile.lock
pod install

# Clean Xcode (in Xcode)
Product → Clean Build Folder (Cmd+Shift+K)
```

## ✅ Success Indicators

Your app is working correctly when you see:

### **In Terminal:**
- Metro bundler running on port 8081
- "Build succeeded" message
- iOS Simulator launching

### **In iOS Simulator:**
- Tennis Tournament app icon appears
- App launches showing tournament list
- 69+ tennis tournaments displayed
- Touch navigation works between screens
- Pull-to-refresh updates data

### **App Functionality:**
- **Tournament List Screen**: Scrollable list with tournaments
- **Tournament Events Screen**: Live match data with status indicators
- **Navigation**: Smooth transitions between screens
- **Data**: Same tournament data as web app (localhost:3000)

## 🎾 Expected App Behavior

### **Tournament List Screen:**
- Shows 69+ tennis tournaments from your Spring Boot backend
- Pull down to refresh tournament data
- Tap any tournament to navigate to events screen
- Native iOS styling and animations

### **Tournament Events Screen:**
- Displays events for selected tournament
- Status indicators: 🔴 LIVE, ✅ Finished, ⏰ Scheduled
- Player names and scores when available
- Pull down to refresh event data
- Back button returns to tournament list

## 🚀 Performance Expectations

- **App Launch**: 2-3 seconds on simulator
- **Tournament List Load**: 1-2 seconds (depends on backend)
- **Navigation**: Instant transitions
- **Pull-to-Refresh**: 1-2 seconds to update data

## 📱 Testing Checklist

- [ ] App launches without crashes
- [ ] Tournament list displays data
- [ ] Touch navigation works
- [ ] Pull-to-refresh functions
- [ ] Back navigation works
- [ ] Data matches web app
- [ ] No console errors in Metro bundler

## 🎯 Final Notes

- **Backend Required**: Ensure Spring Boot is running on port 8080
- **Metro Bundler**: Must be running for development
- **First Build**: May take 5-10 minutes
- **Subsequent Builds**: Much faster (30 seconds - 2 minutes)
- **Simulator vs Device**: Simulator is easier for development

Your React Native Tennis Tournament app is fully functional once these build issues are resolved!