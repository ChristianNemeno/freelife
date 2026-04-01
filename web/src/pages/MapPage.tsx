import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { APIProvider, Map as GoogleMap, AdvancedMarker } from '@vis.gl/react-google-maps';
import { fetchGroupMembers } from '../api';
import { useSignalR } from '../hooks/useSignalR';
import { useSession } from '../SessionContext';
import type { ChatMessage, LocationMarker } from '../types';
import { getInitials, getUserColor } from '../utils/markerUtils';

const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY ?? '';

export function MapPage() {
  const navigate = useNavigate();
  const { session, clearSession } = useSession();

  const [nameMap, setNameMap] = useState<Map<string, string>>(new Map());
  const [markers, setMarkers] = useState<Map<string, LocationMarker>>(new Map());
  const [sharing, setSharing] = useState(false);
  const [chatOpen, setChatOpen] = useState(true);
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
      {/* Header */}
      <div className="map-header">
        <span className="map-group-name">{session.groupName}</span>
        <div className="map-header-actions">
          <button className="btn btn-sm btn-ghost chat-toggle-btn" onClick={handleChatToggle}>
            {chatOpen ? 'Hide Chat' : 'Chat'}
            {unread > 0 && <span className="chat-badge">{unread}</span>}
          </button>
          <button
            className={`btn btn-sm ${sharing ? 'btn-danger' : 'btn-primary'} map-header-share-btn`}
            onClick={handleSharingToggle}
          >
            {sharing ? 'Stop Sharing' : 'Share Location'}
          </button>
          <button className="btn btn-sm btn-ghost" onClick={handleLeave}>
            Leave
          </button>
        </div>
      </div>

      {/* Body: chat left + map right */}
      <div className="map-body">
        {/* Chat panel — in-flow on left */}
        {chatOpen && (
          <div className="chat-panel">
            <div className="chat-panel-header">
              <span>Chat</span>
              <button className="chat-close-btn" onClick={handleChatToggle}>✕</button>
            </div>
            <div className="chat-messages">
              {messages.length === 0 && (
                <p className="chat-empty">No messages yet.<br />Say hi!</p>
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
            <div className="chat-share-row">
              <button
                className={`btn btn-sm ${sharing ? 'btn-danger' : 'btn-primary'} chat-share-btn`}
                onClick={handleSharingToggle}
              >
                {sharing ? 'Stop Sharing Location' : 'Share Location'}
              </button>
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

        {/* Google Map with 3D tilt */}
        <div className="map-container">
          <APIProvider apiKey={GOOGLE_MAPS_API_KEY}>
            <GoogleMap
              defaultCenter={{ lat: 10.3157, lng: 123.8854 }}
              defaultZoom={14}
              defaultTilt={45}
              defaultHeading={0}
              mapId="e697ea99deb4aaf99897e227"
              gestureHandling="greedy"
              disableDefaultUI={false}
              style={{ width: '100%', height: '100%' }}
            >
              {markerEntries.map(([userId, marker]) => {
                const name = nameMap.get(userId) ?? userId;
                const initials = getInitials(name);
                const color = getUserColor(userId);
                return (
                  <AdvancedMarker
                    key={userId}
                    position={{ lat: marker.latitude, lng: marker.longitude }}
                    title={name}
                  >
                    <div
                      className="marker-bubble"
                      style={{ background: color }}
                    >
                      {initials}
                    </div>
                  </AdvancedMarker>
                );
              })}
            </GoogleMap>
          </APIProvider>
        </div>
      </div>
    </div>
  );
}
