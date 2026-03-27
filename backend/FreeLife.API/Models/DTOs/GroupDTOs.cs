namespace FreeLife.API.Models.DTOs;

public record CreateGroupRequest(string Name);

public record JoinGroupRequest(string InviteCode);

public record GroupResponse(int Id, string Name, string InviteCode, int MemberCount);

public record MemberResponse(int UserId, string Name, string Email);
