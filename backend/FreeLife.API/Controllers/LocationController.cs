using System.Security.Claims;
using FreeLife.API.Data;
using FreeLife.API.Models;
using FreeLife.API.Models.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FreeLife.API.Controllers;

[ApiController]
[Authorize]
[Route("api/location")]
public class LocationController : ControllerBase
{
    private readonly AppDbContext _dbContext;

    public LocationController(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    [HttpPost]
    public async Task<IActionResult> UpdateLocation(UpdateLocationRequest request)
    {
        var userId = GetCurrentUserId();

        var location = new Location
        {
            UserId = userId,
            Latitude = request.Latitude,
            Longitude = request.Longitude,
            Timestamp = DateTime.UtcNow
        };

        _dbContext.Locations.Add(location);
        await _dbContext.SaveChangesAsync();

        return NoContent();
    }

    [HttpGet("{userId:int}")]
    public async Task<ActionResult<LocationResponse>> GetLatestLocation(int userId)
    {
        var requesterId = GetCurrentUserId();

        if (requesterId != userId)
        {
            var sharedGroup = await _dbContext.GroupMembers
                .Where(gm => gm.UserId == requesterId)
                .AnyAsync(gm => _dbContext.GroupMembers
                    .Any(gm2 => gm2.GroupId == gm.GroupId && gm2.UserId == userId));

            if (!sharedGroup)
                return Forbid();
        }

        var location = await _dbContext.Locations
            .Where(l => l.UserId == userId)
            .OrderByDescending(l => l.Timestamp)
            .Select(l => new LocationResponse(
                l.UserId,
                l.User.Name,
                l.Latitude,
                l.Longitude,
                l.Timestamp))
            .FirstOrDefaultAsync();

        if (location is null)
        {
            return NotFound("Location not found.");
        }

        return Ok(location);
    }

    private int GetCurrentUserId()
    {
        var claimValue = User.FindFirstValue(ClaimTypes.NameIdentifier);
        return int.Parse(claimValue!);
    }
}
