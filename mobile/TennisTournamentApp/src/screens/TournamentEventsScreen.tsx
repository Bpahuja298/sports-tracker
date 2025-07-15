import React from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  RefreshControl,
  TouchableOpacity,
} from 'react-native';
import { useTournamentEvents, TournamentEvent, formatDateTime } from '../services/api';

interface Props {
  route: {
    params: {
      tournamentId: string;
      tournamentName: string;
    };
  };
  navigation: any;
}

const TournamentEventsScreen: React.FC<Props> = ({ route, navigation }) => {
  const { tournamentId, tournamentName } = route.params;
  const { events, loading, error, refetch } = useTournamentEvents(tournamentId);

  React.useEffect(() => {
    navigation.setOptions({
      title: tournamentName,
    });
  }, [navigation, tournamentName]);

  const getStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'live':
      case 'in_progress':
        return '#28a745';
      case 'finished':
      case 'completed':
        return '#6c757d';
      case 'scheduled':
      case 'upcoming':
        return '#007bff';
      case 'cancelled':
        return '#dc3545';
      default:
        return '#6c757d';
    }
  };

  const getStatusText = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'live':
      case 'in_progress':
        return '🔴 LIVE';
      case 'finished':
      case 'completed':
        return '✅ Finished';
      case 'scheduled':
      case 'upcoming':
        return '⏰ Scheduled';
      case 'cancelled':
        return '❌ Cancelled';
      default:
        return status || 'Unknown';
    }
  };

  const renderEvent = ({ item }: { item: TournamentEvent }) => {
    const eventName = item.participants && item.participants.length >= 2
      ? `${item.participants[0]} vs ${item.participants[1]}`
      : `Event ${item.eventId}`;

    return (
      <View style={styles.eventCard}>
        <View style={styles.eventHeader}>
          <Text style={styles.eventName}>{eventName}</Text>
          <View style={[styles.statusBadge, { backgroundColor: getStatusColor(item.status) }]}>
            <Text style={styles.statusText}>{getStatusText(item.status)}</Text>
          </View>
        </View>

        <Text style={styles.eventDateTime}>
          📅 {formatDateTime(item.eventTime)}
        </Text>

        {item.round && (
          <Text style={styles.eventRound}>🏆 {item.round}</Text>
        )}

        {item.participants && item.participants.length >= 2 && (
          <View style={styles.playersContainer}>
            <Text style={styles.playersLabel}>Players:</Text>
            <Text style={styles.playersText}>
              {item.participants[0]} vs {item.participants[1]}
            </Text>
          </View>
        )}

        {item.score && (
          <View style={styles.scoreContainer}>
            <Text style={styles.scoreLabel}>Score:</Text>
            <Text style={styles.scoreText}>{item.score}</Text>
          </View>
        )}
      </View>
    );
  };

  if (error) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>❌ {error}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={refetch}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {loading && events.length === 0 ? (
        <View style={styles.centerContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Loading events...</Text>
        </View>
      ) : events.length === 0 ? (
        <View style={styles.centerContainer}>
          <Text style={styles.noEventsText}>📅 No events found for this tournament</Text>
        </View>
      ) : (
        <FlatList
          data={events}
          renderItem={renderEvent}
          keyExtractor={(item) => item.eventId}
          contentContainerStyle={styles.listContainer}
          refreshControl={
            <RefreshControl
              refreshing={loading}
              onRefresh={refetch}
              colors={['#007AFF']}
              tintColor="#007AFF"
            />
          }
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  listContainer: {
    paddingHorizontal: 16,
    paddingVertical: 16,
  },
  eventCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  eventHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  eventName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    flex: 1,
    marginRight: 12,
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  eventDateTime: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  eventRound: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  playersContainer: {
    marginBottom: 8,
  },
  playersLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  playersText: {
    fontSize: 14,
    color: '#666',
  },
  scoreContainer: {
    marginTop: 8,
  },
  scoreLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  scoreText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '600',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#666',
  },
  errorText: {
    fontSize: 16,
    color: '#dc3545',
    textAlign: 'center',
    marginBottom: 20,
  },
  noEventsText: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  retryButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  retryButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default TournamentEventsScreen;