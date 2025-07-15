# 📱 Testing React Native App on Physical iPhone

## 🔧 Setup for Physical iPhone Testing

### 1. **iPhone Setup**
- **Connect iPhone** to Mac via USB cable
- **Trust this computer** when prompted on iPhone
- **Enable Developer Mode** on iPhone:
  - Settings → Privacy & Security → Developer Mode → Enable

### 2. **Xcode Setup**
- Open **Xcode**
- Go to **File → Open**
- Navigate to: `mobile/TennisTournamentApp/ios/`
- Open: `TennisTournamentApp.xcworkspace`

### 3. **Select Your iPhone**
- In Xcode top toolbar, click device selector
- Choose your connected iPhone from the list
- If not visible, check USB connection and trust settings

### 4. **Apple Developer Account (Required)**
For physical device testing, you need:
- **Apple ID** (free developer account works)
- Go to **Xcode → Settings → Accounts**
- Add your Apple ID
- Select your team in project settings

### 5. **Run on iPhone**
- Click **▶️ Play button** in Xcode
- App will build and install on your iPhone
- First time may require **"Trust Developer"** on iPhone:
  - Settings → General → VPN & Device Management → Trust

## 🎾 What You'll See on Your iPhone

### **Tournament List Screen:**
- Native iOS interface with your tournament data
- Smooth scrolling through 69+ tennis tournaments
- Pull down to refresh functionality
- Tap any tournament to view events

### **Tournament Events Screen:**
- Live match data with status indicators:
  - 🔴 **LIVE** matches
  - ✅ **Finished** matches
  - ⏰ **Scheduled** matches
- Pull down to refresh event data
- Native back navigation

## 🚀 Alternative: Use iOS Simulator

If physical device setup is complex, the iOS Simulator is running:
- **iPhone 16 Pro Simulator** should be launching now
- Same app functionality as physical device
- Easier for development and testing

## ✅ Success Indicators

Your React Native app is working if you see:
- Tournament list loads with real data
- Smooth navigation between screens
- Pull-to-refresh updates data
- Same tournament data as web app (localhost:3000)
- Native iOS look and feel

## 🎯 App Features Working

✅ **Real-time tournament data** from Spring Boot backend  
✅ **Native mobile UI** with touch interactions  
✅ **Pull-to-refresh** on both screens  
✅ **Stack navigation** between tournament list and events  
✅ **Live match status** indicators  
✅ **Error handling** and loading states  

The mobile app successfully uses your existing backend APIs while providing a native iOS experience!