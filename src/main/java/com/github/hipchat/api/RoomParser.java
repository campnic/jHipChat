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

package com.github.hipchat.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class RoomParser
{
    public static List<Room> parseRoomList(HipChat origin, InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();

        List<Room> rooms = null;

        jp.nextToken(); // START_OBJECT
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            if ("rooms".equals(tag))
            {
                jp.nextToken(); // START_ARRAY
                while (jp.nextToken() != JsonToken.END_ARRAY)
                {
                    Room r = parseRoom(origin, mapper, jp);

                    if (rooms == null)
                    {
                        rooms = new ArrayList<Room>();
                    }

                    rooms.add(r);
                }
            }
        }

        if (rooms == null)
        {
            rooms = Collections.emptyList();
        }

        return rooms;

    }

    public static Room parseRoom(HipChat origin, InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();
        Room room = null;

        jp.nextToken(); // START_OBJECT
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            if ("room".equals(tag))
            {
                jp.nextToken(); // START_OBJECT
                room = parseRoom(origin, mapper, jp);
            }
        }

        return room;

    }

    private static Room parseRoom(HipChat origin, ObjectMapper mapper, JsonParser jp) throws JsonParseException, JsonMappingException, IOException
    {
        Room result = null;

        // Map<String, Object> roomData = mapper.readValue(jp, new
        // TypeReference<Map<String, Object>>()
        // {
        // });
        //
        // if (roomData != null)
        // {
        String roomId = null; // = roomData.get("room_id");
        String name = null;// (String) roomData.get("name");
        String topic = null;// (String) roomData.get("topic");
        Integer lastActive = null;// (Integer) roomData.get("last_active");
        Integer created = null; // (Integer) roomData.get("created");
        String ownerId = null; // roomData.get("owner_user_id");
        Boolean isArchived = null; // (Boolean) roomData.get("is_archived");
        Boolean isPrivate = null; // (Boolean) roomData.get("is_private");
        String xmppJId = null; // (String) roomData.get("xmpp_jid");
        String guestAccessUrl = null;// (String)
                                     // roomData.get("guest_access_url");
        List<UserId> participants = null;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            jp.nextToken();
            if ("room_id".equals(tag))
            {
                roomId = UtilParser.parseString(jp);
            } else if ("name".equals(tag))
            {
                name = UtilParser.parseString(jp);
            } else if ("topic".equals(tag))
            {
                topic = UtilParser.parseString(jp);
            } else if ("last_active".equals(tag))
            {
                lastActive = jp.getIntValue();
            } else if ("created".equals(tag))
            {
                created = jp.getIntValue();
            } else if ("owner_user_id".equals(tag))
            {
                ownerId = UtilParser.parseString(jp);
            } else if ("is_archived".equals(tag))
            {
                isArchived = jp.getBooleanValue();
            } else if ("is_private".equals(tag))
            {
                isPrivate = jp.getBooleanValue();
            } else if ("xmpp_jid".equals(tag))
            {
                xmppJId = UtilParser.parseString(jp);
            } else if ("guest_access_url".equals(tag))
            {
                guestAccessUrl = UtilParser.parseString(jp);
            } else if ("participants".equals(tag))
            {
                while (jp.nextToken() != JsonToken.END_ARRAY)
                {
                    Object userId = null;
                    String userName = null;
                    while (jp.nextToken() != JsonToken.END_OBJECT)
                    {

                        tag = jp.getText();
                        jp.nextToken();
                        if ("user_id".equals(tag))
                        {
                            userId = UtilParser.parseString(jp);
                        } else if ("name".equals(tag))
                        {
                            userName = UtilParser.parseString(jp);
                        }
                    }

                    if (participants == null)
                    {
                        participants = new ArrayList<UserId>();
                    }

                    participants.add(UserId.create(userId.toString(), userName));
                }

            }

        }

        result = Room.create(roomId.toString(), origin, name, topic, lastActive, created, ownerId.toString(), isArchived, isPrivate, xmppJId, participants,
                guestAccessUrl);

        return result;
    }
}
