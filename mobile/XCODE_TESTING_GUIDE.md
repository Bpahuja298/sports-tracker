# 🎾 Running React Native App in Xcode

## 📱 Step-by-Step Instructions:

### 1. **Make Sure Metro Bundler is Running**
First, ensure the React Native development server is running:
```bash
# In terminal (should already be running)
cd mobile/TennisTournamentApp
npm start
```

### 2. **Open the React Native Project in Xcode**
You need to open the actual iOS project file:
- **Close Xcode Settings** if it's open
- In Xcode, go to **File → Open**
- Navigate to: `mobile/TennisTournamentApp/ios/`
- **Open the file**: `TennisTournamentApp.xcworkspace` (NOT .xcodeproj)
- This will open your React Native project

### 3. **Install iOS Simulator (if needed)**
If you don't see simulators available:
- Go to **Xcode → Settings → Platforms**
- Look for **iOS** platform
- Click **"Get"** or **"Install"** next to iOS to download the simulator
- Wait for download to complete

### 4. **Select Simulator and Run**
Now you should see the play button in the top toolbar:
- Look at the top toolbar in Xcode
- You'll see a device selector (next to the ▶️ play button)
- Click on it and select an iOS Simulator (e.g., "iPhone 15", "iPhone 14", etc.)

### 3. **Build and Run**
- Click the **▶️ Play button** (or press `Cmd + R`)
- Xcode will build the project and launch the iOS Simulator
- The Tennis Tournament app should appear on the simulator

### 4. **What You Should See**
Once the app launches:
- **Tournament List Screen** with 69+ tennis tournaments
- **Pull down to refresh** functionality
- **Tap any tournament** to view events
- **Tournament Events Screen** with live match data
- **Status indicators**: 🔴 LIVE, ✅ Finished, ⏰ Scheduled

### 5. **Testing the App**
Try these interactions:
- **Scroll through tournaments** - should be smooth
- **Pull down on tournament list** - refreshes data
- **Tap a tournament** - navigates to events screen
- **Pull down on events screen** - refreshes event data
- **Tap back button** - returns to tournament list

### 6. **Troubleshooting**

#### **If Build Fails:**
- Make sure Metro bundler is running (`npm start`)
- Clean build: **Product → Clean Build Folder** (Cmd + Shift + K)
- Try building again

#### **If Simulator Doesn't Open:**
- Go to **Xcode → Open Developer Tool → Simulator**
- Or install iOS Simulator: **Xcode → Settings → Platforms**

#### **If App Shows Error Screen:**
- Check that your Spring Boot backend is running on port 8080
- The app needs the backend for tournament data

### 7. **Expected Behavior**
✅ **App launches successfully**  
✅ **Shows tournament list with real data**  
✅ **Navigation works between screens**  
✅ **Pull-to-refresh updates data**  
✅ **Same data as web app at localhost:3000**  

## 🎯 Success!
If you see the tournament list with tennis tournaments and can navigate to events, your React Native mobile app is working perfectly!

The app uses the same Spring Boot backend as your web version, so you'll see identical tournament and match data.