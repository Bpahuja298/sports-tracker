# 🎾 Tennis Tournament & Event Viewer

A comprehensive tennis tournament and event tracking application with both web and mobile versions. Built with Spring Boot backend and React/React Native frontends. View real-time tennis tournaments and events from around the world using RapidAPI integration.

## 🚀 Features

- **70+ Tennis Tournaments**: Browse tournaments across ATP, WTA, Challenger, and more
- **Real-time Event Data**: Live tournament events with participant information
- **Cross-Platform**: Web app (React) and mobile app (React Native)
- **Smart Error Handling**: Proper handling of tournaments with no scheduled matches
- **Clean UI**: Simplified interface focused on tournament viewing
- **RapidAPI Integration**: Reliable data fetching with RestTemplate
- **Mobile Features**: Pull-to-refresh, native navigation, touch-optimized UI

## 🏗️ Architecture

### Backend (Spring Boot 3.2.5)
- **Java 17** with Maven
- **H2 Database** for development
- **RestTemplate** for HTTP communication
- **RapidAPI** integration for tennis data
- **Static tournament data** for reliability
- **Cross-platform API** serving both web and mobile

### Web Frontend (React)
- **React 18** with modern hooks
- **Responsive design** with CSS
- **Real-time data updates**
- **Error state handling**

### Mobile Frontend (React Native)
- **TypeScript** for type safety
- **React Navigation** for native navigation
- **Native UI components** (FlatList, TouchableOpacity)
- **Pull-to-refresh** functionality
- **Cross-platform** (iOS & Android)

## 📋 Prerequisites

- **Java 17** or higher
- **Node.js 16** or higher
- **Maven 3.6** or higher
- **RapidAPI Key** (for live data)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Bpahuja298/sports-tracker.git
cd sports-tracker
```

### 2. Backend Setup
```bash
# Install dependencies and compile
mvn clean compile

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will start on `http://localhost:3000`

### 4. Mobile App Setup (React Native)
```bash
# Navigate to mobile app directory
cd mobile/TennisTournamentApp

# Install dependencies
npm install

# Start Metro bundler
npm start

# In another terminal, run on iOS (requires Xcode)
npx react-native run-ios

# Or run on Android (requires Android Studio)
npx react-native run-android
```

📱 **Mobile App Features:**
- Native iOS and Android apps
- Pull-to-refresh functionality
- Touch-optimized tournament browsing
- Native navigation between screens
- Same backend API integration as web app

For detailed mobile setup instructions, see [`mobile/MOBILE_SETUP_GUIDE.md`](mobile/MOBILE_SETUP_GUIDE.md)

## 🔧 Configuration

### RapidAPI Setup
1. Get your API key from [RapidAPI](https://rapidapi.com/)
2. Update the API key in [`TournamentService.java`](src/main/java/com/bha/sportsTracker/service/TournamentService.java):
```java
.header("X-RapidAPI-Key", "YOUR_API_KEY_HERE")
```

### Application Properties
The application uses H2 in-memory database by default. Configuration can be found in [`application.properties`](src/main/resources/application.properties).

## 📡 API Endpoints

### Tournaments
- `GET /api/tournaments` - Get all available tournaments
- `GET /api/tournaments/{id}/events` - Get events for a specific tournament

### Example Response
```json
{
  "tournamentId": 2555,
  "name": "Wimbledon Men Singles",
  "events": [
    {
      "eventId": "id1200255561449511",
      "eventTime": "2025-07-13T20:30",
      "participants": ["Jannik Sinner", "Carlos Alcaraz"],
      "status": "scheduled"
    }
  ]
}
```

## 🎯 Usage

1. **Start both applications** (backend on :8080, frontend on :3000)
2. **Browse tournaments** from the dropdown menu
3. **View events** for selected tournaments
4. **See real-time data** for active tournaments
5. **Handle empty states** gracefully for tournaments with no matches

## 🏆 Supported Tournaments

The application includes 70+ tournaments including:
- **ATP Tours**: Wimbledon, US Open, French Open, Australian Open
- **WTA Tours**: Major women's tournaments
- **Challenger Events**: Professional challenger circuits
- **Doubles Tournaments**: Both men's and women's doubles

## 🔍 Key Features Explained

### Smart Error Handling
- **404 Responses**: "No matches found" → Empty array (not mock data)
- **400 Responses**: "Tournament not active" → Empty array
- **Network Errors**: Graceful fallback with user-friendly messages

### Static Tournament Data
- **Reliability**: 70 tournaments loaded from static data
- **Performance**: No API calls for tournament list
- **Consistency**: Always available tournament information

### Real-time Events
- **Live Data**: Events fetched from RapidAPI
- **Proper Parsing**: Handles RapidAPI's numbered object format
- **Name Formatting**: "Sinner, Jannik" → "Jannik Sinner"

## 🧪 Testing

### Backend Testing
```bash
# Test tournament API
curl http://localhost:8080/api/tournaments

# Test specific tournament events
curl http://localhost:8080/api/tournaments/2555/events
```

### Frontend Testing
1. Open `http://localhost:3000`
2. Select different tournaments from dropdown
3. Verify events display correctly
4. Test tournaments with no events show "No Matches Scheduled"

## 📁 Project Structure

```
sports-tracker/
├── src/main/java/com/bha/sportsTracker/
│   ├── controller/          # REST controllers
│   ├── service/            # Business logic
│   ├── entity/             # JPA entities
│   └── repository/         # Data repositories
├── frontend/
│   ├── src/
│   │   ├── components/     # React components
│   │   └── services/       # API services
│   └── public/             # Static assets
└── README.md
```

## 🚀 Deployment

### Backend Deployment
```bash
# Create production JAR
mvn clean package

# Run production build
java -jar target/sports-tracker-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
```bash
# Create production build
cd frontend
npm run build

# Serve static files (example with serve)
npx serve -s build
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **RapidAPI** for providing tennis tournament data
- **Spring Boot** for the robust backend framework
- **React** for the dynamic frontend experience
- **Tennis community** for inspiration

## 📞 Support

For support, email [your-email@example.com] or create an issue in the GitHub repository.

---

**Built with ❤️ for tennis enthusiasts worldwide** 🎾