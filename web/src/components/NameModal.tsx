import { useRef, useState, type FormEvent } from 'react';

interface NameModalProps {
  inviteCode: string;
  onConfirm: (displayName: string) => void;
  onCancel: () => void;
  loading?: boolean;
  error?: string | null;
}

export function NameModal({ inviteCode, onConfirm, onCancel, loading, error }: NameModalProps) {
  const [name, setName] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const trimmed = name.trim();
    if (trimmed) onConfirm(trimmed);
  };

  return (
    <div className="modal-backdrop" onClick={onCancel}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal-title">Join Group</h2>
        <p className="modal-subtitle">
          Code: <strong>{inviteCode}</strong>
        </p>
        <form onSubmit={handleSubmit} className="modal-form">
          <label className="modal-label" htmlFor="display-name">
            Your display name
          </label>
          <input
            ref={inputRef}
            id="display-name"
            className="modal-input"
            type="text"
            placeholder="e.g. Alice"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={40}
            autoFocus
            disabled={loading}
          />
          {error && <p className="modal-error">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="btn btn-ghost" onClick={onCancel} disabled={loading}>
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading || !name.trim()}
            >
              {loading ? 'Joining…' : 'Join'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
