namespace FreeLife.API.Models.DTOs;

public record GuestTokenRequest(string InviteCode, string DisplayName);
public record GuestTokenResponse(string Token, string GuestId, int GroupId, string GroupName, string InviteCode);
public record GuestMemberResponse(string UserId, string Name);
public record PublicGroupResponse(int Id, string Name, string InviteCode, int MemberCount);
