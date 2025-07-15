import React from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
  RefreshControl,
  Alert,
} from 'react-native';
import { useTournaments, Tournament } from '../services/api';

interface Props {
  navigation: any;
}

const TournamentListScreen: React.FC<Props> = ({ navigation }) => {
  const { tournaments, loading, error, refetch } = useTournaments();

  const handleTournamentPress = (tournament: Tournament) => {
    navigation.navigate('TournamentEvents', {
      tournamentId: tournament.tournamentId,
      tournamentName: tournament.name,
    });
  };

  const renderTournament = ({ item }: { item: Tournament }) => (
    <TouchableOpacity
      style={styles.tournamentCard}
      onPress={() => handleTournamentPress(item)}
    >
      <Text style={styles.tournamentName}>{item.name}</Text>
      {item.location && (
        <Text style={styles.tournamentLocation}>📍 {item.location}</Text>
      )}
      {item.startDate && item.endDate && (
        <Text style={styles.tournamentDates}>
          {new Date(item.startDate).toLocaleDateString()} - {new Date(item.endDate).toLocaleDateString()}
        </Text>
      )}
      <Text style={styles.viewEvents}>Tap to view events →</Text>
    </TouchableOpacity>
  );

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
      <Text style={styles.header}>🎾 Tennis Tournaments</Text>
      
      {loading && tournaments.length === 0 ? (
        <View style={styles.centerContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Loading tournaments...</Text>
        </View>
      ) : (
        <FlatList
          data={tournaments}
          renderItem={renderTournament}
          keyExtractor={(item) => item.tournamentId}
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
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
  },
  listContainer: {
    paddingHorizontal: 16,
    paddingBottom: 20,
  },
  tournamentCard: {
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
  tournamentName: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  tournamentLocation: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  tournamentDates: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  viewEvents: {
    fontSize: 14,
    color: '#007AFF',
    fontWeight: '500',
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

export default TournamentListScreen;