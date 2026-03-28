import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useCallback, useEffect, useRef, useState } from 'react';
import { MapContainer, Marker, TileLayer, Tooltip } from 'react-leaflet';
import { useNavigate } from 'react-router-dom';
import { fetchGroupMembers } from '../api';
import { useSignalR } from '../hooks/useSignalR';
import { useSession } from '../SessionContext';
import type { ChatMessage, LocationMarker } from '../types';
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
  const [chatOpen, setChatOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [unread, setUnread] = useState(0);
  const [chatInput, setChatInput] = useState('');
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

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

  const handleReceiveMessage = useCallback((userId: string, name: string, text: string, timestamp: string) => {
    const msg: ChatMessage = { id: `${userId}-${timestamp}`, userId, name, text, timestamp };
    setMessages((prev) => [...prev, msg]);
    setChatOpen((open) => {
      if (!open) setUnread((n) => n + 1);
      return open;
    });
  }, []);

  // Scroll to bottom when messages change and panel is open
  useEffect(() => {
    if (chatOpen) messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, chatOpen]);

  const handleReceiveLocation = useCallback((userId: string, lat: number, lng: number, timestamp: string) => {
    setMarkers((prev) => {
      const next = new Map(prev);
      next.set(userId, { userId, name: '', latitude: lat, longitude: lng, updatedAt: timestamp });
      return next;
    });
  }, []);

  const { sendLocation, sendMessage } = useSignalR({
    token: session?.token ?? '',
    groupId: session?.groupId ?? 0,
    onUserInfo: handleUserInfo,
    onUserJoined: handleUserJoined,
    onReceiveLocation: handleReceiveLocation,
    onReceiveMessage: handleReceiveMessage,
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

  const handleChatToggle = () => {
    setChatOpen((open) => {
      if (!open) setUnread(0);
      return !open;
    });
  };

  const handleSendMessage = () => {
    const text = chatInput.trim();
    if (!text) return;
    sendMessage(text);
    setChatInput('');
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
          <button className="btn btn-sm btn-ghost chat-toggle-btn" onClick={handleChatToggle}>
            Chat
            {unread > 0 && <span className="chat-badge">{unread}</span>}
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

      {/* Chat panel */}
      {chatOpen && (
        <div className="chat-panel">
          <div className="chat-panel-header">
            <span>Group Chat</span>
            <button className="chat-close-btn" onClick={handleChatToggle}>✕</button>
          </div>
          <div className="chat-messages">
            {messages.length === 0 && (
              <p className="chat-empty">No messages yet. Say hi!</p>
            )}
            {messages.map((msg) => {
              const isOwn = msg.userId === session.guestId;
              return (
                <div key={msg.id} className={`chat-msg ${isOwn ? 'chat-msg--own' : ''}`}>
                  {!isOwn && <span className="chat-msg-name">{msg.name}</span>}
                  <div className="chat-msg-bubble">{msg.text}</div>
                  <span className="chat-msg-time">
                    {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
              );
            })}
            <div ref={messagesEndRef} />
          </div>
          <div className="chat-input-row">
            <input
              className="chat-input"
              type="text"
              placeholder="Message…"
              maxLength={500}
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') handleSendMessage(); }}
            />
            <button className="btn btn-sm btn-primary" onClick={handleSendMessage} disabled={!chatInput.trim()}>
              Send
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
