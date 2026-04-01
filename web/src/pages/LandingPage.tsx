import { useEffect, useRef, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchPublicGroups, joinGroup } from '../api';
import { GroupCard } from '../components/GroupCard';
import { NameModal } from '../components/NameModal';
import { useSession } from '../SessionContext';
import type { PublicGroup } from '../types';

export function LandingPage() {
  const navigate = useNavigate();
  const { setSession } = useSession();

  const [groups, setGroups] = useState<PublicGroup[]>([]);
  const [groupsLoading, setGroupsLoading] = useState(true);
  const [groupsError, setGroupsError] = useState<string | null>(null);

  const [inviteInput, setInviteInput] = useState('');
  const [pendingCode, setPendingCode] = useState<string | null>(null);
  const [joinLoading, setJoinLoading] = useState(false);
  const [joinError, setJoinError] = useState<string | null>(null);

  const inviteInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchPublicGroups()
      .then(setGroups)
      .catch(() => setGroupsError('Could not load public groups.'))
      .finally(() => setGroupsLoading(false));
  }, []);

  const handleJoinCard = (inviteCode: string) => {
    setJoinError(null);
    setPendingCode(inviteCode);
  };

  const handleInviteSubmit = (e: FormEvent) => {
    e.preventDefault();
    const code = inviteInput.trim().toUpperCase();
    if (!code) return;
    setJoinError(null);
    setPendingCode(code);
  };

  const handleConfirmName = async (displayName: string) => {
    if (!pendingCode) return;
    setJoinLoading(true);
    setJoinError(null);
    try {
      const session = await joinGroup(pendingCode, displayName);
      setSession(session);
      navigate('/map');
    } catch (err) {
      setJoinError(err instanceof Error ? err.message : 'Something went wrong.');
    } finally {
      setJoinLoading(false);
    }
  };

  const handleCancelModal = () => {
    if (!joinLoading) {
      setPendingCode(null);
      setJoinError(null);
    }
  };

  return (
    <div className="landing">
      {/* Header */}
      <header className="landing-header">
        <div className="landing-header-inner">
          <div className="logo">
            <span className="logo-icon">ASNU</span>
            <span className="logo-text">ASNU</span>
          </div>
        </div>
      </header>

      <main className="landing-main">
        {/* Hero section */}
        <section className="hero">
          <div className="hero-badge">
            <span className="hero-live-dot" />
            Live
          </div>
          <h1 className="hero-title">Asa na uy?!</h1>
          <p className="hero-description">
            Real-time group location sharing — see your friends, family, or teammates on an
            interactive map, share your location instantly, and stay connected with built-in
            group chat. No accounts needed, just join with an invite code.
          </p>
          <div className="hero-features">
            <span className="feature-pill">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
              Live map
            </span>
            <span className="feature-pill">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
              Group chat
            </span>
            <span className="feature-pill">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              Invite codes
            </span>
            <span className="feature-pill">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              No sign-up
            </span>
          </div>
        </section>

        {/* Public groups section */}
        <section className="section">
          <h2 className="section-title">Active Groups</h2>
          <p className="section-subtitle">Join a public group to see member locations in real time.</p>

          {groupsLoading && (
            <div className="cards-loading">
              {[0, 1, 2].map((i) => (
                <div key={i} className="group-card group-card--skeleton" />
              ))}
            </div>
          )}

          {groupsError && <p className="error-text">{groupsError}</p>}

          {!groupsLoading && !groupsError && groups.length === 0 && (
            <p className="empty-text">No public groups right now. Enter an invite code below.</p>
          )}

          {!groupsLoading && groups.length > 0 && (
            <div className="cards-grid">
              {groups.map((g) => (
                <GroupCard key={g.id} group={g} onJoin={handleJoinCard} />
              ))}
            </div>
          )}
        </section>

        {/* Divider */}
        <div className="divider">
          <span>or enter an invite code</span>
        </div>

        {/* Invite code input */}
        <section className="section section--narrow">
          <form className="invite-form" onSubmit={handleInviteSubmit}>
            <input
              ref={inviteInputRef}
              className="invite-input"
              type="text"
              placeholder="XXXXXX"
              value={inviteInput}
              onChange={(e) => setInviteInput(e.target.value.toUpperCase())}
              maxLength={6}
              spellCheck={false}
            />
            <button
              type="submit"
              className="btn btn-primary"
              disabled={inviteInput.trim().length !== 6}
            >
              Join
            </button>
          </form>
        </section>
      </main>

      {/* Name modal */}
      {pendingCode && (
        <NameModal
          inviteCode={pendingCode}
          onConfirm={handleConfirmName}
          onCancel={handleCancelModal}
          loading={joinLoading}
          error={joinError}
        />
      )}
    </div>
  );
}
