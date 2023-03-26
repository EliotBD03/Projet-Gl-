package main.be.ac.umons.g02.data_object;

public record Notification(String notificationId, String senderId, String receiverId, String contractId,
                           String proposalName, String providerProposalId, String context, String ean, String address,
                           String creationDate) {
}
