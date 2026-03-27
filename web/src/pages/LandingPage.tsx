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
            <span className="logo-icon">FL</span>
            <span className="logo-text">FreeLife</span>
          </div>
          <p className="logo-tagline">Live location sharing for groups</p>
        </div>
      </header>

      <main className="landing-main">
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
