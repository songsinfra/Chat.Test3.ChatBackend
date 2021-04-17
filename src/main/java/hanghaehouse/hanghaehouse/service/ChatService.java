package hanghaehouse.hanghaehouse.service;

import hanghaehouse.hanghaehouse.domain.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService { //입장, 퇴장 처리

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomService chatRoomService;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        System.out.println("메세지 발송 단계 진입");
        chatMessage.setUserCount(chatRoomService.getUserCount(chatMessage.getRoomId()));
        System.out.println("보낼 유저 수 셋 완료");
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            System.out.println(chatMessage);
            chatMessage.setMessage(chatMessage.getUserName() + "님이 방에 입장했습니다.");
            chatMessage.setUserName("[알림]");
            System.out.println(chatMessage);
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            System.out.println(chatMessage);
            chatMessage.setMessage(chatMessage.getUserName() + "님이 방에서 나갔습니다.");
            chatMessage.setUserName("[알림]");
            System.out.println(chatMessage);
        }
        System.out.println("전송 요청");
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        System.out.println("전송 완료");
    }

}