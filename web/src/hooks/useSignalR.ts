import { HubConnection, HubConnectionBuilder, HubConnectionState } from '@microsoft/signalr';
import { useCallback, useEffect, useRef } from 'react';
import { API_URL } from '../api';

interface UseSignalROptions {
  token: string;
  groupId: number;
  onUserInfo: (userId: string, name: string) => void;
  onUserJoined: (userId: string) => void;
  onReceiveLocation: (userId: string, lat: number, lng: number, timestamp: string) => void;
}

export function useSignalR(options: UseSignalROptions) {
  const connectionRef = useRef<HubConnection | null>(null);
  const { token, groupId, onUserInfo, onUserJoined, onReceiveLocation } = options;

  // Stable callback refs so we don't tear down the connection on every render
  const onUserInfoRef = useRef(onUserInfo);
  const onUserJoinedRef = useRef(onUserJoined);
  const onReceiveLocationRef = useRef(onReceiveLocation);
  onUserInfoRef.current = onUserInfo;
  onUserJoinedRef.current = onUserJoined;
  onReceiveLocationRef.current = onReceiveLocation;

  useEffect(() => {
    const connection = new HubConnectionBuilder()
      .withUrl(`${API_URL}/locationHub?access_token=${token}`)
      .withAutomaticReconnect()
      .build();

    connection.on('UserInfo', (userId: string, name: string) => {
      onUserInfoRef.current(userId, name);
    });

    connection.on('UserJoined', (userId: string) => {
      onUserJoinedRef.current(userId);
    });

    connection.on('ReceiveLocation', (userId: string, lat: number, lng: number, timestamp: string) => {
      onReceiveLocationRef.current(userId, lat, lng, timestamp);
    });

    connectionRef.current = connection;

    connection
      .start()
      .then(() => connection.invoke('JoinGroup', groupId.toString()))
      .catch((err) => console.error('SignalR connection error:', err));

    return () => {
      if (
        connection.state === HubConnectionState.Connected ||
        connection.state === HubConnectionState.Connecting ||
        connection.state === HubConnectionState.Reconnecting
      ) {
        connection.invoke('LeaveGroup', groupId.toString()).catch(() => null).finally(() => connection.stop());
      } else {
        connection.stop();
      }
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, groupId]);

  const sendLocation = useCallback((lat: number, lng: number) => {
    const conn = connectionRef.current;
    if (conn && conn.state === HubConnectionState.Connected) {
      conn.invoke('SendLocation', groupId.toString(), lat, lng).catch(console.error);
    }
  }, [groupId]);

  return { sendLocation };
}
