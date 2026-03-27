using System.Security.Claims;
using FreeLife.API.Data;
using FreeLife.API.Models.DTOs;
using FreeLife.API.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FreeLife.API.Controllers;

[ApiController]
[Route("api/guest")]
public class GuestController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly GuestTokenService _guestTokenService;

    public GuestController(AppDbContext db, GuestTokenService guestTokenService)
    {
        _db = db;
        _guestTokenService = guestTokenService;
    }

    // GET /api/guest/groups - returns up to 3 most recent public groups
    [HttpGet("groups")]
    public async Task<ActionResult<List<PublicGroupResponse>>> GetPublicGroups()
    {
        var groups = await _db.Groups
            .Where(g => g.IsPublic)
            .OrderByDescending(g => g.CreatedAt)
            .Take(3)
            .Select(g => new PublicGroupResponse(
                g.Id,
                g.Name,
                g.InviteCode,
                g.Members.Count))
            .ToListAsync();
        return Ok(groups);
    }

    // POST /api/guest/token - validate invite code and issue guest JWT
    [HttpPost("token")]
    public async Task<ActionResult<GuestTokenResponse>> GetGuestToken(GuestTokenRequest request)
    {
        var displayName = request.DisplayName.Trim();
        var inviteCode = request.InviteCode.Trim().ToUpperInvariant();

        if (string.IsNullOrWhiteSpace(displayName))
            return BadRequest("Display name is required.");
        if (inviteCode.Length != 6)
            return BadRequest("Invite code must be 6 characters.");

        var group = await _db.Groups.SingleOrDefaultAsync(g => g.InviteCode == inviteCode);
        if (group is null)
            return NotFound("Group not found.");

        var guestId = $"guest_{Guid.NewGuid():N}";
        var token = _guestTokenService.GenerateGuestToken(guestId, displayName);

        return Ok(new GuestTokenResponse(token, guestId, group.Id, group.Name, group.InviteCode));
    }

    // GET /api/guest/group-members?inviteCode=ABC123 - returns member names (requires guest JWT)
    [Authorize]
    [HttpGet("group-members")]
    public async Task<ActionResult<List<GuestMemberResponse>>> GetGroupMembers([FromQuery] string inviteCode)
    {
        var callerId = User.FindFirstValue(ClaimTypes.NameIdentifier)!;
        if (!callerId.StartsWith("guest_"))
            return Forbid();

        var group = await _db.Groups
            .Include(g => g.Members).ThenInclude(m => m.User)
            .SingleOrDefaultAsync(g => g.InviteCode == inviteCode.ToUpperInvariant());

        if (group is null)
            return NotFound();

        return Ok(group.Members.Select(m => new GuestMemberResponse(m.UserId.ToString(), m.User.Name)).ToList());
    }
}
