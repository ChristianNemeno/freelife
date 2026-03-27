using System.Security.Claims;
using System.Security.Cryptography;
using FreeLife.API.Data;
using FreeLife.API.Models;
using FreeLife.API.Models.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FreeLife.API.Controllers;

[ApiController]
[Authorize]
[Route("api/groups")]
public class GroupsController : ControllerBase
{
    private const string InviteCodeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private readonly AppDbContext _dbContext;

    public GroupsController(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    [HttpGet]
    public async Task<ActionResult<List<GroupResponse>>> GetGroups()
    {
        var userId = GetCurrentUserId();

        var groups = await _dbContext.Groups
            .Where(g => g.Members.Any(m => m.UserId == userId))
            .Select(g => new GroupResponse(
                g.Id,
                g.Name,
                g.InviteCode,
                g.Members.Count))
            .ToListAsync();

        return Ok(groups);
    }

    [HttpPost]
    public async Task<ActionResult<GroupResponse>> CreateGroup(CreateGroupRequest request)
    {
        var name = request.Name.Trim();
        if (string.IsNullOrWhiteSpace(name))
        {
            return BadRequest("Group name is required.");
        }

        var userId = GetCurrentUserId();
        var inviteCode = await GenerateUniqueInviteCodeAsync();

        var group = new Group
        {
            Name = name,
            InviteCode = inviteCode,
            CreatedByUserId = userId
        };

        _dbContext.Groups.Add(group);
        await _dbContext.SaveChangesAsync();

        _dbContext.GroupMembers.Add(new GroupMember
        {
            GroupId = group.Id,
            UserId = userId
        });
        await _dbContext.SaveChangesAsync();

        return Ok(new GroupResponse(group.Id, group.Name, group.InviteCode, 1));
    }

    [HttpPost("join")]
    public async Task<IActionResult> JoinGroup(JoinGroupRequest request)
    {
        var inviteCode = request.InviteCode.Trim().ToUpperInvariant();
        if (string.IsNullOrWhiteSpace(inviteCode))
        {
            return BadRequest("Invite code is required.");
        }

        var userId = GetCurrentUserId();
        var group = await _dbContext.Groups
            .SingleOrDefaultAsync(g => g.InviteCode == inviteCode);

        if (group is null)
        {
            return NotFound("Group not found.");
        }

        var isAlreadyMember = await _dbContext.GroupMembers
            .AnyAsync(gm => gm.GroupId == group.Id && gm.UserId == userId);

        if (isAlreadyMember)
        {
            return Conflict("You are already a member of this group.");
        }

        _dbContext.GroupMembers.Add(new GroupMember
        {
            GroupId = group.Id,
            UserId = userId
        });

        await _dbContext.SaveChangesAsync();
        return NoContent();
    }

    [HttpGet("{id:int}/members")]
    public async Task<ActionResult<List<MemberResponse>>> GetMembers(int id)
    {
        var userId = GetCurrentUserId();

        var isMember = await _dbContext.GroupMembers
            .AnyAsync(gm => gm.GroupId == id && gm.UserId == userId);

        if (!isMember)
        {
            return Forbid();
        }

        var members = await _dbContext.GroupMembers
            .Where(gm => gm.GroupId == id)
            .Select(gm => new MemberResponse(gm.UserId, gm.User.Name, gm.User.Email))
            .ToListAsync();

        return Ok(members);
    }

    [HttpDelete("{id:int}/leave")]
    public async Task<IActionResult> LeaveGroup(int id)
    {
        var userId = GetCurrentUserId();

        var membership = await _dbContext.GroupMembers
            .SingleOrDefaultAsync(gm => gm.GroupId == id && gm.UserId == userId);

        if (membership is null)
        {
            return NotFound("You are not a member of this group.");
        }

        _dbContext.GroupMembers.Remove(membership);
        await _dbContext.SaveChangesAsync();

        return NoContent();
    }

    private int GetCurrentUserId()
    {
        var claimValue = User.FindFirstValue(ClaimTypes.NameIdentifier);
        return int.Parse(claimValue!);
    }

    private async Task<string> GenerateUniqueInviteCodeAsync()
    {
        while (true)
        {
            var inviteCode = RandomNumberGenerator.GetString(InviteCodeAlphabet, 6);
            var exists = await _dbContext.Groups.AnyAsync(g => g.InviteCode == inviteCode);
            if (!exists)
            {
                return inviteCode;
            }
        }
    }
}
