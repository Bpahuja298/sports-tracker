# 🧪 Mobile App Testing Guide

## Current Status
✅ **Backend**: Spring Boot running on port 8080  
✅ **Metro Bundler**: React Native development server running  
✅ **Xcode**: Downloaded and ready  
🔄 **CocoaPods**: Installing iOS dependencies...  

## Testing Steps

### 1. **iOS Simulator Testing**
Once CocoaPods installation completes:

```bash
# Navigate to mobile app directory
cd mobile/TennisTournamentApp

# Launch iOS simulator
npx react-native run-ios
```

**Expected Result:**
- iOS Simulator opens automatically
- Tennis Tournament app launches
- Shows tournament list screen
- Tap tournaments to view events

### 2. **What You'll See**

#### **Tournament List Screen:**
- 🎾 Header: "Tennis Tournaments"
- List of 69+ tennis tournaments
- Each tournament shows:
  - Tournament name
  - Location (if available)
  - Date range
  - "Tap to view events →"

#### **Tournament Events Screen:**
- Tournament name in header
- List of tournament events/matches
- Each event shows:
  - Match name
  - Status badge (🔴 LIVE, ✅ Finished, ⏰ Scheduled)
  - Date and time
  - Players (if available)
  - Score (if available)

### 3. **Interactive Features to Test**

#### **Pull-to-Refresh:**
- Pull down on tournament list → refreshes tournaments
- Pull down on events screen → refreshes events for that tournament

#### **Navigation:**
- Tap any tournament → navigates to events screen
- Tap back button → returns to tournament list
- Header shows tournament name on events screen

#### **Loading States:**
- Loading spinner while fetching data
- Error messages if API fails
- Empty state if no events found

### 4. **Expected API Calls**
When testing, you should see these API calls in backend logs:

```
GET /api/tournaments           # Tournament list screen
GET /api/tournaments/{id}/events  # Tournament events screen
```

### 5. **Troubleshooting**

#### **If iOS Simulator Doesn't Open:**
```bash
# Open Xcode first, then try again
open -a Xcode
npx react-native run-ios
```

#### **If Build Fails:**
```bash
# Clean and rebuild
cd ios
xcodebuild clean
cd ..
npx react-native run-ios
```

#### **If Metro Bundler Issues:**
```bash
# Reset Metro cache
npx react-native start --reset-cache
```

### 6. **Performance Testing**

#### **Scroll Performance:**
- Tournament list should scroll smoothly
- Events list should handle 20+ events without lag

#### **Navigation Performance:**
- Screen transitions should be smooth
- Back navigation should be instant

#### **API Performance:**
- Tournament list loads within 2-3 seconds
- Events load within 1-2 seconds per tournament

### 7. **Cross-Platform Testing**

#### **Android Testing (Optional):**
```bash
# If you have Android Studio installed
npx react-native run-android
```

### 8. **Success Criteria**

✅ **App launches successfully**  
✅ **Tournament list displays 69+ tournaments**  
✅ **Tap navigation works between screens**  
✅ **Pull-to-refresh updates data**  
✅ **Loading states show properly**  
✅ **Error handling works for failed API calls**  
✅ **Back navigation works correctly**  
✅ **Real-time data matches web app**  

### 9. **Next Steps After Testing**

Once basic testing is complete:
- Test on physical device
- Add more mobile-specific features
- Implement push notifications
- Add offline caching
- Deploy to App Store/Google Play

## 🎯 **Testing Focus Areas**

1. **Functionality**: All features work as expected
2. **Performance**: Smooth scrolling and navigation
3. **UI/UX**: Touch-friendly interface
4. **Data Accuracy**: Same data as web app
5. **Error Handling**: Graceful failure handling