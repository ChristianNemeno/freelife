namespace FreeLife.API.Models.DTOs;

public record UpdateLocationRequest(double Latitude, double Longitude);

public record LocationResponse(
    int UserId,
    string UserName,
    double Latitude,
    double Longitude,
    DateTime Timestamp);
