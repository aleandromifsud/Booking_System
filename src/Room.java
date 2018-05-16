public class Room
{
    private int roomNo;
    private int roomCapacity;
    private String roomType;



    Room(int roomNo, int roomCapacity, String roomType)
    {
        this.roomNo = roomNo;
        this.roomCapacity = roomCapacity;
        this.roomType = roomType;
    }

    Room(String roomType)
    {
        this.roomType = roomType;
    }

    Room(int roomNo)
    {
        this.roomNo = roomNo;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
