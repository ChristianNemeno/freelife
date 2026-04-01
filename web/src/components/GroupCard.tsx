import type { PublicGroup } from '../types';

interface GroupCardProps {
  group: PublicGroup;
  onJoin: (inviteCode: string) => void;
}

export function GroupCard({ group, onJoin }: GroupCardProps) {
  return (
    <div className="group-card">
      <div className="group-card-header">
        <span className="group-card-icon">
          {group.name.slice(0, 2).toUpperCase()}
        </span>
        <div className="group-card-info">
          <h3 className="group-card-name">{group.name}</h3>
          <span className="group-card-members">
            <span className="live-dot" />
            {group.memberCount} {group.memberCount === 1 ? 'member' : 'members'}
          </span>
        </div>
      </div>
      <div className="group-card-footer">
        <code className="group-card-code">{group.inviteCode}</code>
        <button
          className="btn btn-primary btn-sm"
          onClick={() => onJoin(group.inviteCode)}
        >
          Join
        </button>
      </div>
    </div>
  );
}
