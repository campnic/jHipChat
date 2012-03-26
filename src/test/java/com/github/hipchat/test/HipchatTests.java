/*   
 * Copyright [2012] [Nicholas Campion]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Authored by Nick Campion campnic@gmail.com
 */

package com.github.hipchat.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.github.hipchat.api.HipChat;
import com.github.hipchat.api.Room;
import com.github.hipchat.api.User;
import com.github.hipchat.api.UserId;
import com.github.hipchat.api.messages.HistoryMessage;
import com.github.hipchat.api.messages.Message.Color;

public class HipchatTests
{
    // ENVIRONMENT DEPENDENT
    // most tests will require an admin key
    private static String HIPCHAT_KEY = "";
    // insert a valid hipchat user id here
    private static String TEST_ROOM_OWNER = "";
    // insert a valid hipchat room id here
    private static String TEST_ROOM_ID = "";
    private static String TEST_ROOM_NAME = "APITestRoom";

    @Test
    public void testListRooms()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        List<Room> rooms = hipchat.listRooms();
        assertNotNull(rooms);
        assertFalse(rooms.isEmpty());
    }

    @Test
    public void testCreateAndDeleteRooms()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);

        List<Room> roomsBefore = hipchat.listRooms();
        int countBeforeOp = roomsBefore.size();

        Room room = null;
        try
        {
            room = hipchat.createRoom(TEST_ROOM_NAME, TEST_ROOM_OWNER, false, "test out api", false);
        } catch (IOException e)
        {
            e.printStackTrace();
            fail("Exception!");
        }
        assertNotNull(room);
        assertEquals(TEST_ROOM_NAME, room.getName());

        List<Room> roomsAfterAdd = hipchat.listRooms();
        int countAfterOp = roomsAfterAdd.size();
        assertEquals(countBeforeOp + 1, countAfterOp);

        boolean deleted = hipchat.deleteRoom(room.getId());
        assertTrue(deleted);

        List<Room> roomsAfterDelete = hipchat.listRooms();
        int countAfterOp2 = roomsAfterDelete.size();
        assertEquals(countBeforeOp, countAfterOp2);
    }

    @Test
    public void testGetRoom()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        Room room = hipchat.getRoom(TEST_ROOM_ID);
        assertNotNull(room);

    }

    @Test
    public void testGetRoomHistory()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        Room room = hipchat.getRoom(TEST_ROOM_ID);
        assertNotNull(room);
        List<HistoryMessage> messages = room.getHistory();
        assertNotNull(messages);
        assertTrue(messages.size() > 0);
    }

    @Test
    public void testMessageRoom()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        Room room = hipchat.getRoom(TEST_ROOM_ID);
        assertNotNull(room);
        UserId from = UserId.create("api", "API UnitTest");
        assertTrue(room.sendMessage("test message", from, false, Color.PURPLE));
    }

    @Test
    public void testCreateAndDeleteUser()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        User user = hipchat.createUser("test@unittest.com", "Test McTester", "UnitTestUser", false, "ch4ngeM3", (TimeZone) null);
        assertNotNull(user);
        assertTrue(hipchat.deleteUser(user));
    }

    @Test
    public void testListUsers()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        List<User> users = hipchat.listUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    public void testGetUser()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        UserId id = UserId.create(TEST_ROOM_OWNER);
        User user = hipchat.getUser(id);

        assertNotNull(user);
        assertEquals(user, id);
    }

    @Test
    public void testUpdateUser()
    {
        HipChat hipchat = new HipChat(HIPCHAT_KEY);
        UserId id = UserId.create(TEST_ROOM_OWNER);
        User user = hipchat.getUser(id);

        assertNotNull(user);
        assertEquals(user, id);

        String existingTitle = user.getTitle();

        user = hipchat.updateUser(user, null, null, "Temporary Tester", user.isGroupAdmin(), null, null);

        assertEquals("Temporary Tester", user.getTitle());

        user = hipchat.updateUser(user, null, null, existingTitle, user.isGroupAdmin(), null, null);
    }

}
