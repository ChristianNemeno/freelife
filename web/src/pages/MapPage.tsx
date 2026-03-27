import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useCallback, useEffect, useRef, useState } from 'react';
import { MapContainer, Marker, TileLayer, Tooltip } from 'react-leaflet';
import { useNavigate } from 'react-router-dom';
import { fetchGroupMembers } from '../api';
import { useSignalR } from '../hooks/useSignalR';
import { useSession } from '../SessionContext';
import type { LocationMarker } from '../types';
import { getInitials, getUserColor } from '../utils/markerUtils';

function createMarkerIcon(name: string, userId: string): L.DivIcon {
  const color = getUserColor(userId);
  const initials = getInitials(name);
  return L.divIcon({
    className: '',
    html: `<div class="marker-bubble" style="background:${color}">${initials}</div>`,
    iconSize: [40, 40],
    iconAnchor: [20, 20],
  });
}

export function MapPage() {
  const navigate = useNavigate();
  const { session, clearSession } = useSession();

  const [nameMap, setNameMap] = useState<Map<string, string>>(new Map());
  const [markers, setMarkers] = useState<Map<string, LocationMarker>>(new Map());
  const [sharing, setSharing] = useState(false);

  const watchIdRef = useRef<number | null>(null);
  const lastSendRef = useRef<number>(0);

  // Redirect if no session
  useEffect(() => {
    if (!session) navigate('/', { replace: true });
  }, [session, navigate]);

  // Fetch existing members on mount
  useEffect(() => {
    if (!session) return;
    fetchGroupMembers(session.inviteCode, session.token)
      .then((members) => {
        setNameMap((prev) => {
          const next = new Map(prev);
          members.forEach((m) => next.set(m.userId, m.name));
          return next;
        });
      })
      .catch(console.error);
  }, [session]);

  const handleUserInfo = useCallback((userId: string, name: string) => {
    setNameMap((prev) => {
      if (prev.get(userId) === name) return prev;
      const next = new Map(prev);
      next.set(userId, name);
      return next;
    });
  }, []);

  const handleUserJoined = useCallback((_userId: string) => {
    // UserInfo will follow immediately with the display name
  }, []);

  const handleReceiveLocation = useCallback((userId: string, lat: number, lng: number, timestamp: string) => {
    setMarkers((prev) => {
      const next = new Map(prev);
      next.set(userId, { userId, name: '', latitude: lat, longitude: lng, updatedAt: timestamp });
      return next;
    });
  }, []);

  const { sendLocation } = useSignalR({
    token: session?.token ?? '',
    groupId: session?.groupId ?? 0,
    onUserInfo: handleUserInfo,
    onUserJoined: handleUserJoined,
    onReceiveLocation: handleReceiveLocation,
  });

  // Share location toggle
  const handleSharingToggle = () => {
    if (!sharing) {
      if (!navigator.geolocation) {
        alert('Geolocation is not supported by your browser.');
        return;
      }
      watchIdRef.current = navigator.geolocation.watchPosition(
        (pos) => {
          const now = Date.now();
          if (now - lastSendRef.current >= 5000) {
            lastSendRef.current = now;
            sendLocation(pos.coords.latitude, pos.coords.longitude);
          }
        },
        (err) => console.error('Geolocation error:', err),
        { enableHighAccuracy: true }
      );
      setSharing(true);
    } else {
      if (watchIdRef.current !== null) {
        navigator.geolocation.clearWatch(watchIdRef.current);
        watchIdRef.current = null;
      }
      setSharing(false);
    }
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (watchIdRef.current !== null) {
        navigator.geolocation.clearWatch(watchIdRef.current);
      }
    };
  }, []);

  const handleLeave = () => {
    clearSession();
    navigate('/');
  };

  if (!session) return null;

  const markerEntries = Array.from(markers.entries());

  return (
    <div className="map-page">
      {/* Fixed header */}
      <div className="map-header">
        <span className="map-group-name">{session.groupName}</span>
        <div className="map-header-actions">
          <button
            className={`btn btn-sm ${sharing ? 'btn-danger' : 'btn-primary'}`}
            onClick={handleSharingToggle}
          >
            {sharing ? 'Stop Sharing' : 'Share Location'}
          </button>
          <button className="btn btn-sm btn-ghost" onClick={handleLeave}>
            Leave
          </button>
        </div>
      </div>

      {/* Map */}
      <div className="map-container">
        <MapContainer
          center={[20, 0]}
          zoom={2}
          style={{ height: '100%', width: '100%' }}
          zoomControl={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {markerEntries.map(([userId, marker]) => {
            const name = nameMap.get(userId) ?? userId;
            const icon = createMarkerIcon(name, userId);
            return (
              <Marker
                key={userId}
                position={[marker.latitude, marker.longitude]}
                icon={icon}
              >
                <Tooltip permanent={false} direction="top" offset={[0, -22]}>
                  {name}
                </Tooltip>
              </Marker>
            );
          })}
        </MapContainer>
      </div>
    </div>
  );
}
