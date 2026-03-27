export interface PublicGroup {
  id: number;
  name: string;
  inviteCode: string;
  memberCount: number;
}

export interface GuestSession {
  token: string;
  guestId: string;
  groupId: number;
  groupName: string;
  inviteCode: string;
}

export interface GuestMember {
  userId: string;
  name: string;
}

export interface LocationMarker {
  userId: string;
  name: string;
  latitude: number;
  longitude: number;
  updatedAt: string;
}
