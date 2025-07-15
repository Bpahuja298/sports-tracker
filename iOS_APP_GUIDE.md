# 📱 iOS App Development Guide for Tennis Tournament Viewer

## 🎯 Overview

This guide outlines how to create an iOS app version of your existing tennis tournament viewer web application. The iOS app will consume the same Spring Boot backend APIs while providing a native mobile experience.

## 🏗️ Architecture Overview

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    HTTP/API    ┌─────────────────┐
│   iOS App       │ ◄──────────────► │ Spring Boot API │ ◄─────────────► │   RapidAPI      │
│   (Swift/UIKit) │                 │   (Port 8080)   │                │  (External)     │
└─────────────────┘                 └─────────────────┘                └─────────────────┘
```

## 📋 Prerequisites

### Development Environment
- **macOS** (required for iOS development)
- **Xcode 15+** (latest version recommended)
- **iOS 16.0+** target deployment
- **Swift 5.9+**
- **Apple Developer Account** (for device testing and App Store)

### Backend Requirements
- Your existing Spring Boot backend running on `http://localhost:8080`
- Or deployed backend with public URL

## 🚀 Step-by-Step Implementation Plan

### Phase 1: Project Setup

#### 1.1 Create New iOS Project
```bash
# Open Xcode and create new project
# Choose: iOS > App
# Product Name: TennisTournamentViewer
# Interface: UIKit (or SwiftUI)
# Language: Swift
```

#### 1.2 Project Structure
```
TennisTournamentViewer/
├── Models/
│   ├── Tournament.swift
│   ├── TournamentEvent.swift
│   └── APIResponse.swift
├── Services/
│   ├── NetworkManager.swift
│   ├── TournamentService.swift
│   └── APIEndpoints.swift
├── ViewControllers/
│   ├── TournamentListViewController.swift
│   ├── TournamentEventsViewController.swift
│   └── EventDetailViewController.swift
├── Views/
│   ├── TournamentCell.swift
│   ├── EventCell.swift
│   └── EmptyStateView.swift
├── Utils/
│   ├── Constants.swift
│   ├── Extensions.swift
│   └── DateFormatter+Extensions.swift
└── Resources/
    ├── Assets.xcassets
    └── Info.plist
```

### Phase 2: Data Models

#### 2.1 Tournament Model
```swift
// Models/Tournament.swift
import Foundation

struct Tournament: Codable {
    let tournamentId: Int
    let name: String
    
    enum CodingKeys: String, CodingKey {
        case tournamentId
        case name
    }
}
```

#### 2.2 Tournament Event Model
```swift
// Models/TournamentEvent.swift
import Foundation

struct TournamentEvent: Codable {
    let eventId: String
    let eventTime: String
    let participants: [String]
    let status: String
    
    enum CodingKeys: String, CodingKey {
        case eventId
        case eventTime
        case participants
        case status
    }
    
    var formattedEventTime: Date? {
        let formatter = ISO8601DateFormatter()
        return formatter.date(from: eventTime)
    }
    
    var participantsString: String {
        return participants.joined(separator: " vs ")
    }
}
```

### Phase 3: Network Layer

#### 3.1 API Endpoints
```swift
// Services/APIEndpoints.swift
import Foundation

struct APIEndpoints {
    static let baseURL = "http://localhost:8080/api"
    // For production: static let baseURL = "https://your-backend-url.com/api"
    
    static let tournaments = "\(baseURL)/tournaments"
    
    static func tournamentEvents(tournamentId: Int) -> String {
        return "\(baseURL)/tournaments/\(tournamentId)/events"
    }
}
```

#### 3.2 Network Manager
```swift
// Services/NetworkManager.swift
import Foundation

class NetworkManager {
    static let shared = NetworkManager()
    private let session = URLSession.shared
    
    private init() {}
    
    func request<T: Codable>(
        url: String,
        method: HTTPMethod = .GET,
        responseType: T.Type,
        completion: @escaping (Result<T, NetworkError>) -> Void
    ) {
        guard let url = URL(string: url) else {
            completion(.failure(.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(.requestFailed(error)))
                return
            }
            
            guard let data = data else {
                completion(.failure(.noData))
                return
            }
            
            do {
                let decodedResponse = try JSONDecoder().decode(responseType, from: data)
                completion(.success(decodedResponse))
            } catch {
                completion(.failure(.decodingFailed(error)))
            }
        }.resume()
    }
}

enum HTTPMethod: String {
    case GET = "GET"
    case POST = "POST"
}

enum NetworkError: Error {
    case invalidURL
    case requestFailed(Error)
    case noData
    case decodingFailed(Error)
    
    var localizedDescription: String {
        switch self {
        case .invalidURL:
            return "Invalid URL"
        case .requestFailed(let error):
            return "Request failed: \(error.localizedDescription)"
        case .noData:
            return "No data received"
        case .decodingFailed(let error):
            return "Decoding failed: \(error.localizedDescription)"
        }
    }
}
```

#### 3.3 Tournament Service
```swift
// Services/TournamentService.swift
import Foundation

class TournamentService {
    static let shared = TournamentService()
    private let networkManager = NetworkManager.shared
    
    private init() {}
    
    func fetchTournaments(completion: @escaping (Result<[Tournament], NetworkError>) -> Void) {
        networkManager.request(
            url: APIEndpoints.tournaments,
            responseType: [Tournament].self,
            completion: completion
        )
    }
    
    func fetchTournamentEvents(
        tournamentId: Int,
        completion: @escaping (Result<[TournamentEvent], NetworkError>) -> Void
    ) {
        let url = APIEndpoints.tournamentEvents(tournamentId: tournamentId)
        networkManager.request(
            url: url,
            responseType: [TournamentEvent].self,
            completion: completion
        )
    }
}
```

### Phase 4: User Interface

#### 4.1 Tournament List View Controller
```swift
// ViewControllers/TournamentListViewController.swift
import UIKit

class TournamentListViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!
    
    private var tournaments: [Tournament] = []
    private let tournamentService = TournamentService.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        fetchTournaments()
    }
    
    private func setupUI() {
        title = "🏆 Tennis Tournaments"
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "TournamentCell")
    }
    
    private func fetchTournaments() {
        loadingIndicator.startAnimating()
        
        tournamentService.fetchTournaments { [weak self] result in
            DispatchQueue.main.async {
                self?.loadingIndicator.stopAnimating()
                
                switch result {
                case .success(let tournaments):
                    self?.tournaments = tournaments
                    self?.tableView.reloadData()
                case .failure(let error):
                    self?.showError(error.localizedDescription)
                }
            }
        }
    }
    
    private func showError(_ message: String) {
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

// MARK: - TableView DataSource & Delegate
extension TournamentListViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tournaments.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TournamentCell", for: indexPath)
        let tournament = tournaments[indexPath.row]
        cell.textLabel?.text = tournament.name
        cell.accessoryType = .disclosureIndicator
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let tournament = tournaments[indexPath.row]
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let eventsVC = storyboard.instantiateViewController(withIdentifier: "TournamentEventsViewController") as? TournamentEventsViewController {
            eventsVC.tournament = tournament
            navigationController?.pushViewController(eventsVC, animated: true)
        }
    }
}
```

#### 4.2 Tournament Events View Controller
```swift
// ViewControllers/TournamentEventsViewController.swift
import UIKit

class TournamentEventsViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!
    @IBOutlet weak var emptyStateView: UIView!
    @IBOutlet weak var emptyStateLabel: UILabel!
    
    var tournament: Tournament!
    private var events: [TournamentEvent] = []
    private let tournamentService = TournamentService.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        fetchEvents()
    }
    
    private func setupUI() {
        title = tournament.name
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "EventCell")
        
        // Setup empty state
        emptyStateLabel.text = "📅 No Matches Scheduled\n\nThis tournament currently has no scheduled matches."
        emptyStateLabel.textAlignment = .center
        emptyStateLabel.numberOfLines = 0
        emptyStateView.isHidden = true
    }
    
    private func fetchEvents() {
        loadingIndicator.startAnimating()
        emptyStateView.isHidden = true
        
        tournamentService.fetchTournamentEvents(tournamentId: tournament.tournamentId) { [weak self] result in
            DispatchQueue.main.async {
                self?.loadingIndicator.stopAnimating()
                
                switch result {
                case .success(let events):
                    self?.events = events
                    if events.isEmpty {
                        self?.showEmptyState()
                    } else {
                        self?.tableView.reloadData()
                    }
                case .failure(let error):
                    self?.showError(error.localizedDescription)
                }
            }
        }
    }
    
    private func showEmptyState() {
        emptyStateView.isHidden = false
        tableView.isHidden = true
    }
    
    private func showError(_ message: String) {
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Retry", style: .default) { _ in
            self.fetchEvents()
        })
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        present(alert, animated: true)
    }
}

// MARK: - TableView DataSource & Delegate
extension TournamentEventsViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "EventCell", for: indexPath)
        let event = events[indexPath.row]
        
        cell.textLabel?.text = event.participantsString
        cell.detailTextLabel?.text = formatEventTime(event.eventTime)
        
        return cell
    }
    
    private func formatEventTime(_ timeString: String) -> String {
        let formatter = ISO8601DateFormatter()
        guard let date = formatter.date(from: timeString) else {
            return timeString
        }
        
        let displayFormatter = DateFormatter()
        displayFormatter.dateStyle = .medium
        displayFormatter.timeStyle = .short
        return "🕐 \(displayFormatter.string(from: date))"
    }
}
```

### Phase 5: Advanced Features

#### 5.1 Pull-to-Refresh
```swift
// Add to TournamentEventsViewController
private func setupRefreshControl() {
    let refreshControl = UIRefreshControl()
    refreshControl.addTarget(self, action: #selector(refreshData), for: .valueChanged)
    tableView.refreshControl = refreshControl
}

@objc private func refreshData() {
    fetchEvents()
    tableView.refreshControl?.endRefreshing()
}
```

#### 5.2 Search Functionality
```swift
// Add to TournamentListViewController
private var filteredTournaments: [Tournament] = []
private var isSearching = false

private func setupSearchController() {
    let searchController = UISearchController(searchResultsController: nil)
    searchController.searchResultsUpdater = self
    searchController.obscuresBackgroundDuringPresentation = false
    searchController.searchBar.placeholder = "Search tournaments..."
    navigationItem.searchController = searchController
    definesPresentationContext = true
}

extension TournamentListViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        guard let searchText = searchController.searchBar.text, !searchText.isEmpty else {
            isSearching = false
            tableView.reloadData()
            return
        }
        
        isSearching = true
        filteredTournaments = tournaments.filter { tournament in
            tournament.name.lowercased().contains(searchText.lowercased())
        }
        tableView.reloadData()
    }
}
```

#### 5.3 Core Data for Offline Storage
```swift
// Add Core Data stack for caching tournaments and events
import CoreData

class CoreDataManager {
    static let shared = CoreDataManager()
    
    lazy var persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "TournamentModel")
        container.loadPersistentStores { _, error in
            if let error = error {
                fatalError("Core Data error: \(error)")
            }
        }
        return container
    }()
    
    var context: NSManagedObjectContext {
        return persistentContainer.viewContext
    }
    
    func saveContext() {
        if context.hasChanges {
            try? context.save()
        }
    }
}
```

### Phase 6: UI/UX Enhancements

#### 6.1 Custom Table View Cells
```swift
// Views/TournamentCell.swift
import UIKit

class TournamentCell: UITableViewCell {
    @IBOutlet weak var tournamentNameLabel: UILabel!
    @IBOutlet weak var tournamentIcon: UIImageView!
    
    func configure(with tournament: Tournament) {
        tournamentNameLabel.text = tournament.name
        tournamentIcon.image = UIImage(systemName: "tennisball.fill")
        tournamentIcon.tintColor = .systemGreen
    }
}
```

#### 6.2 Dark Mode Support
```swift
// Add to ViewDidLoad of each ViewController
override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
    super.traitCollectionDidChange(previousTraitCollection)
    
    if traitCollection.hasDifferentColorAppearance(comparedTo: previousTraitCollection) {
        updateAppearance()
    }
}

private func updateAppearance() {
    // Update colors based on current trait collection
    view.backgroundColor = UIColor.systemBackground
    tableView.backgroundColor = UIColor.systemBackground
}
```

### Phase 7: Testing & Deployment

#### 7.1 Unit Tests
```swift
// TournamentServiceTests.swift
import XCTest
@testable import TennisTournamentViewer

class TournamentServiceTests: XCTestCase {
    var tournamentService: TournamentService!
    
    override func setUp() {
        super.setUp()
        tournamentService = TournamentService.shared
    }
    
    func testFetchTournaments() {
        let expectation = XCTestExpectation(description: "Fetch tournaments")
        
        tournamentService.fetchTournaments { result in
            switch result {
            case .success(let tournaments):
                XCTAssertFalse(tournaments.isEmpty)
                expectation.fulfill()
            case .failure(let error):
                XCTFail("Failed to fetch tournaments: \(error)")
            }
        }
        
        wait(for: [expectation], timeout: 10.0)
    }
}
```

#### 7.2 UI Tests
```swift
// TournamentViewUITests.swift
import XCTest

class TournamentViewUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        app = XCUIApplication()
        app.launch()
    }
    
    func testTournamentListNavigation() {
        let tableView = app.tables.firstMatch
        XCTAssertTrue(tableView.exists)
        
        let firstCell = tableView.cells.firstMatch
        if firstCell.exists {
            firstCell.tap()
            
            // Verify navigation to events screen
            XCTAssertTrue(app.navigationBars.count > 1)
        }
    }
}
```

## 🚀 Deployment Steps

### 1. App Store Preparation
```bash
# 1. Update version and build numbers
# 2. Create app icons (1024x1024 for App Store)
# 3. Create screenshots for different device sizes
# 4. Write app description and keywords
```

### 2. Backend Deployment
```bash
# Deploy your Spring Boot backend to cloud service
# Update APIEndpoints.swift with production URL
# Ensure CORS is configured for mobile app
```

### 3. TestFlight Distribution
```bash
# Archive app in Xcode
# Upload to App Store Connect
# Create TestFlight build
# Invite beta testers
```

## 📱 Key iOS-Specific Features to Add

### 1. Push Notifications
```swift
// For live match updates
import UserNotifications

class NotificationManager {
    static let shared = NotificationManager()
    
    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            // Handle permission result
        }
    }
}
```

### 2. Background App Refresh
```swift
// For updating tournament data in background
import BackgroundTasks

class BackgroundTaskManager {
    func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "com.yourapp.refresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 minutes
        
        try? BGTaskScheduler.shared.submit(request)
    }
}
```

### 3. Widget Support
```swift
// Create Today Extension for quick tournament info
import WidgetKit
import SwiftUI

struct TournamentWidget: Widget {
    let kind: String = "TournamentWidget"
    
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            TournamentWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Tennis Tournaments")
        .description("View upcoming tennis matches")
    }
}
```

## 🎯 Next Steps

1. **Start with Phase 1-3**: Set up project, models, and network layer
2. **Build basic UI**: Implement tournament list and events screens
3. **Test with your backend**: Ensure API integration works
4. **Add advanced features**: Search, offline storage, push notifications
5. **Polish UI/UX**: Custom cells, animations, dark mode
6. **Test thoroughly**: Unit tests, UI tests, device testing
7. **Deploy**: TestFlight → App Store

## 📚 Additional Resources

- [Apple Developer Documentation](https://developer.apple.com/documentation/)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
- [Swift Programming Language Guide](https://docs.swift.org/swift-book/)
- [URLSession Tutorial](https://www.raywenderlich.com/3244963-urlsession-tutorial-getting-started)
- [Core Data Tutorial](https://www.raywenderlich.com/7569-getting-started-with-core-data-tutorial)

This guide provides a complete roadmap for creating your iOS tennis tournament viewer app! 🎾📱