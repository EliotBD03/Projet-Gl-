package main.be.ac.umons.g02.data_object;
//Extension Claire
public record Invitation(String invitationId, String senderId, String receiverId, String address,
                           String permission, String nameSender, String type) {
}
