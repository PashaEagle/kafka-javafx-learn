package io.reflectoring.kafka;

import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.sender.KafkaSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
class MainServiceTest {

    private static final String USERNAME1 = "USERNAME1";
    private static final String USERNAME2 = "USERNAME2";
    private static final String USERNAME3 = "USERNAME3";

    private static final long DEFAULT_TIMESTAMP = 100L;
    private static final String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";

    private static final Message u1u2Message = new Message(USERNAME1, USERNAME2, DEFAULT_MESSAGE, DEFAULT_TIMESTAMP);
    private static final Message u2u1Message = new Message(USERNAME2, USERNAME1, DEFAULT_MESSAGE, DEFAULT_TIMESTAMP);
    private static final Message u2u3Message = new Message(USERNAME2, USERNAME3, DEFAULT_MESSAGE, DEFAULT_TIMESTAMP);
    private static final Message u3u2Message = new Message(USERNAME3, USERNAME2, DEFAULT_MESSAGE, DEFAULT_TIMESTAMP);

    MainService objectUnderTest = new MainService();

    @Mock
    private KafkaSender kafkaSender;
    private Map<ChatId, List<Message>> chatIdToMessagesMap;
    private Map<String, List<String>> usernameToChattersMap;
    private Map<String, List<Integer>> loggedUsernameToClientPorts;

    @BeforeEach
    void setUp() {
        chatIdToMessagesMap = new HashMap<>();
        usernameToChattersMap = new HashMap<>();
        loggedUsernameToClientPorts = new HashMap<>();
        kafkaSender = mock(KafkaSender.class);


        chatIdToMessagesMap.put(new ChatId(USERNAME1, USERNAME2), List.of(u1u2Message, u2u1Message));
        chatIdToMessagesMap.put(new ChatId(USERNAME2, USERNAME3), List.of(u2u3Message, u3u2Message));

        usernameToChattersMap.put(USERNAME1, List.of(USERNAME2));
        usernameToChattersMap.put(USERNAME2, List.of(USERNAME1, USERNAME3));
        usernameToChattersMap.put(USERNAME3, List.of(USERNAME2));

        loggedUsernameToClientPorts.put(USERNAME1, new ArrayList<>(List.of(8000)));
        loggedUsernameToClientPorts.put(USERNAME2, new ArrayList<>(List.of(8001)));
        loggedUsernameToClientPorts.put(USERNAME3, new ArrayList<>(List.of(8002)));

        objectUnderTest.setChatIdToMessagesMap(chatIdToMessagesMap);
        objectUnderTest.setLoggedUsernameToClientPorts(loggedUsernameToClientPorts);
        objectUnderTest.setUsernameToChattersMap(usernameToChattersMap);
        objectUnderTest.setKafkaSender(kafkaSender);
    }

    @Test
    void getAllUserMessagesTest() {
        Map<String, List<Message>> actualResult = objectUnderTest.getAllUserMessages(USERNAME2);
        Map<String, List<Message>> expectedResult = new HashMap<>();
        expectedResult.put(USERNAME1, List.of(u1u2Message, u2u1Message));
        expectedResult.put(USERNAME3, List.of(u2u3Message, u3u2Message));
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void addLoggedClientTest() {
        objectUnderTest.addLoggedClient("USERNAME4", 8003);
        assertEquals(List.of(8003), objectUnderTest.getLoggedUsernameToClientPorts().get("USERNAME4"));
    }

    @Test
    void logoutClientTest() {
        objectUnderTest.logoutClient(USERNAME2, 8001);
        assertNull(objectUnderTest.getLoggedUsernameToClientPorts().get(USERNAME2));
    }
}