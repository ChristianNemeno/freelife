using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;

namespace FreeLife.API.Hubs;

[Authorize]
public class LocationHub : Hub
{
    // Adds the current connection to the SignalR group for this app-level group.
    public async Task JoinGroup(string groupId)
    {
        await Groups.AddToGroupAsync(Context.ConnectionId, $"group_{groupId}");

        // Notify everyone in the group, including the caller, that this user joined.
        await Clients.Group($"group_{groupId}")
            .SendAsync("UserJoined", Context.UserIdentifier);

        var displayName = Context.User!.FindFirstValue(ClaimTypes.Name) ?? Context.UserIdentifier!;
        await Clients.Group($"group_{groupId}").SendAsync("UserInfo", Context.UserIdentifier, displayName);
    }

    // Removes only this connection from the SignalR group.
    public async Task LeaveGroup(string groupId)
    {
        await Groups.RemoveFromGroupAsync(Context.ConnectionId, $"group_{groupId}");
    }

    // Broadcasts the caller's latest coordinates to other users in the same group.
    public async Task SendLocation(string groupId, double latitude, double longitude)
    {
        await Clients.OthersInGroup($"group_{groupId}")
            .SendAsync(
                "ReceiveLocation",
                Context.UserIdentifier,
                latitude,
                longitude,
                DateTime.UtcNow.ToString("O"));
    }

    public override Task OnDisconnectedAsync(Exception? exception)
    {
        // SignalR automatically removes the connection from all groups on disconnect.
        // Avoid broadcasting presence globally because that would leak group membership.
        return base.OnDisconnectedAsync(exception);
    }
}
