import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import TournamentListScreen from '../screens/TournamentListScreen';
import TournamentEventsScreen from '../screens/TournamentEventsScreen';

export type RootStackParamList = {
  TournamentList: undefined;
  TournamentEvents: {
    tournamentId: string;
    tournamentName: string;
  };
};

const Stack = createStackNavigator<RootStackParamList>();

const AppNavigator: React.FC = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="TournamentList"
        screenOptions={{
          headerStyle: {
            backgroundColor: '#007AFF',
          },
          headerTintColor: '#fff',
          headerTitleStyle: {
            fontWeight: 'bold',
          },
          headerBackTitle: '',
        }}
      >
        <Stack.Screen
          name="TournamentList"
          component={TournamentListScreen}
          options={{
            title: 'Tennis Tournaments',
          }}
        />
        <Stack.Screen
          name="TournamentEvents"
          component={TournamentEventsScreen}
          options={({ route }) => ({
            title: route.params?.tournamentName || 'Tournament Events',
          })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;