namespace FreeLife.API.Models;

public class GroupMember
{
    public int Id { get; set; }

    public int UserId { get; set; }

    public int GroupId { get; set; }

    public User User { get; set; } = null!;

    public Group Group { get; set; } = null!;
}
