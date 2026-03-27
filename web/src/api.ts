import type { GuestMember, GuestSession, PublicGroup } from './types';

export const API_URL = import.meta.env.VITE_API_URL ?? 'http://34.126.112.84:8080';

export async function fetchPublicGroups(): Promise<PublicGroup[]> {
  const res = await fetch(`${API_URL}/api/guest/groups`);
  if (!res.ok) throw new Error('Failed to fetch public groups');
  return res.json();
}

export async function joinGroup(inviteCode: string, displayName: string): Promise<GuestSession> {
  const res = await fetch(`${API_URL}/api/guest/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ inviteCode, displayName }),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Failed to join group');
  }
  return res.json();
}

export async function fetchGroupMembers(inviteCode: string, token: string): Promise<GuestMember[]> {
  const res = await fetch(`${API_URL}/api/guest/group-members?inviteCode=${encodeURIComponent(inviteCode)}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error('Failed to fetch group members');
  return res.json();
}
