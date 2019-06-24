package chat.tamtam.botapi.queries;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import chat.tamtam.botapi.TamTamIntegrationTest;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.AttachmentNotReadyException;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachment;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.Chat;
import chat.tamtam.botapi.model.ChatType;
import chat.tamtam.botapi.model.ContactAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachmentRequestPayload;
import chat.tamtam.botapi.model.FileAttachment;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;
import chat.tamtam.botapi.model.Intent;
import chat.tamtam.botapi.model.LinkButton;
import chat.tamtam.botapi.model.LinkedMessage;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageLinkType;
import chat.tamtam.botapi.model.MessageList;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.NewMessageLink;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.RequestContactButton;
import chat.tamtam.botapi.model.RequestGeoLocationButton;
import chat.tamtam.botapi.model.SendMessageResult;
import chat.tamtam.botapi.model.UploadEndpoint;
import chat.tamtam.botapi.model.UploadType;
import chat.tamtam.botapi.model.UploadedFileInfo;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.UserWithPhoto;
import chat.tamtam.botapi.model.VideoAttachment;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author alexandrchuprin
 */
public class SendVideoMessageQueryIntegrationTest extends TamTamIntegrationTest {
    @Test
    public void shouldSendVideo() throws Exception {
        String uploadUrl = getUploadUrl(UploadType.VIDEO);
        File file = new File(getClass().getClassLoader().getResource("test.mp4").toURI());
        UploadedInfo uploadedInfo = uploadAPI.uploadAV(uploadUrl, file).execute();
        AttachmentRequest attach = new VideoAttachmentRequest(uploadedInfo);
        NewMessageBody newMessage = new NewMessageBody(null, Collections.singletonList(attach), null);
        send(newMessage, getChatsForSend());
    }

    @Test
    public void shouldSendOwnVideoById() throws Exception {
        String uploadUrl = getUploadUrl(UploadType.VIDEO);
        File file = new File(getClass().getClassLoader().getResource("test.mp4").toURI());
        UploadedInfo uploadedInfo = uploadAPI.uploadAV(uploadUrl, file).execute();
        AttachmentRequest attach = new VideoAttachmentRequest(uploadedInfo);
        NewMessageBody newMessage = new NewMessageBody(null, Collections.singletonList(attach), null);
        List<Message> createdMessages = send(newMessage, getChatsForSend());
        for (Message createdMessage : createdMessages) {
            VideoAttachment attachment = (VideoAttachment) createdMessage.getBody().getAttachments().get(0);
            AttachmentRequest copyAttach = new VideoAttachmentRequest(
                    new UploadedInfo(attachment.getPayload().getId()));

            doSend(new NewMessageBody("resend with attach", Collections.singletonList(copyAttach), null),
                    createdMessage.getRecipient().getChatId());
        }
    }

    @Test
    public void shouldSendAnyAccessibleVideoUsingToken() throws Exception {
        UploadEndpoint uploadEndpoint = botAPI.getUploadUrl(UploadType.VIDEO).execute();
        File videoFile = new File(getClass().getClassLoader().getResource("test.mp4").toURI());
        UploadedInfo uploadedInfo = uploadAPI.uploadAV(uploadEndpoint.getUrl(), videoFile).execute();
        AttachmentRequest request = new VideoAttachmentRequest(uploadedInfo);
        NewMessageBody newMessage = new NewMessageBody(null, Collections.singletonList(request), null);
        List<Chat> chats = getChats();
        Chat chat = getByTitle(chats, "test chat #4");
        Chat channel = getByTitle(chats, "test channel #1");
        List<Chat> chatsToSend = Arrays.asList(chat, channel);

        List<Message> createdMessages = new GetMessagesQuery(client2).messageIds(
                send(newMessage, chatsToSend).stream().map(m -> m.getBody().getMid()).collect(
                        Collectors.toSet())).execute().getMessages();

        for (Message createdMessage : createdMessages) {
            VideoAttachment attachment = (VideoAttachment) createdMessage.getBody().getAttachments().get(0);
            VideoAttachmentRequest copyAttach = new VideoAttachmentRequest(
                    new UploadedInfo(attachment.getPayload().getId()).token(attachment.getPayload().getToken()));

            List<Chat> client2Chats = getChats(client2);
            for (Chat c : Arrays.asList(/*getByType(client2Chats, ChatType.DIALOG),*/
                    getByTitle(client2Chats, "test chat #7"), getByTitle(client2Chats, "test channel #5"))) {

                doSend(client2, new NewMessageBody("resent with attach", Collections.singletonList(copyAttach), null),
                        c.getChatId());
            }
        }
    }

    @Test(expected = APIException.class)
    public void shouldNOTSendVideoIfItIsNotAccessible() throws Exception {
        UploadEndpoint uploadEndpoint = botAPI.getUploadUrl(UploadType.VIDEO).execute();
        File file = new File(getClass().getClassLoader().getResource("test.mp4").toURI());
        UploadedInfo uploadedInfo = uploadAPI.uploadAV(uploadEndpoint.getUrl(), file).execute();
        AttachmentRequest request = new VideoAttachmentRequest(uploadedInfo);
        NewMessageBody newMessage = new NewMessageBody(null, Collections.singletonList(request), null);
        List<Chat> chats = getChats();
        Chat chat = getByTitle(chats, "test chat #5"); // no bot 2 in this chat
        List<Chat> chatsToSend = Collections.singletonList(chat);

        List<Message> createdMessages = send(newMessage, chatsToSend);
        for (Message createdMessage : createdMessages) {
            VideoAttachment attachment = (VideoAttachment) createdMessage.getBody().getAttachments().get(0);
            VideoAttachmentRequest copyAttach = new VideoAttachmentRequest(
                    new UploadedInfo(attachment.getPayload().getId()).token(attachment.getPayload().getToken()));

            List<Chat> client2Chats = getChats(client2);
            doSend(client2, new NewMessageBody("resent with attach", Collections.singletonList(copyAttach), null),
                    getByTitle(client2Chats, "test chat #7").getChatId());
        }
    }

    @Test(expected = APIException.class)
    public void shouldNOTSendFileByIdIfNotOwner() throws Exception {
        UploadEndpoint uploadEndpoint = botAPI.getUploadUrl(UploadType.VIDEO).execute();
        File file = new File(getClass().getClassLoader().getResource("test.mp4").toURI());
        UploadedInfo uploadedInfo = uploadAPI.uploadAV(uploadEndpoint.getUrl(), file).execute();
        AttachmentRequest request = new VideoAttachmentRequest(uploadedInfo);
        NewMessageBody newMessage = new NewMessageBody(null, Collections.singletonList(request), null);
        Chat chat = getByTitle(getChats(), "test chat #5"); // no bot 2 in this chat
        doSend(newMessage, chat.getChatId());

        VideoAttachmentRequest copyAttach = new VideoAttachmentRequest(new UploadedInfo(uploadedInfo.getId()));
        List<Chat> client2Chats = getChats(client2);
        doSend(client2, new NewMessageBody("resent with attach", Collections.singletonList(copyAttach), null),
                getByTitle(client2Chats, "test chat #7").getChatId());
    }

    private List<Chat> getChatsForSend() throws Exception {
        List<Chat> chats = getChats();
        Chat dialog = getByType(chats, ChatType.DIALOG);
        Chat chat = getByTitle(chats, "test chat #4");
        Chat channel = getByTitle(chats, "test channel #1");
        return Arrays.asList(dialog, chat, channel);
    }
}