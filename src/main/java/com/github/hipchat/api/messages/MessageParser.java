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

package com.github.hipchat.api.messages;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.hipchat.api.HipChat;
import com.github.hipchat.api.Room;
import com.github.hipchat.api.UserId;
import com.github.hipchat.api.UtilParser;

public class MessageParser
{

    public static List<HistoryMessage> parseRoomHistory(Room origin, InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();

        List<HistoryMessage> messages = null;

        jp.nextToken(); // START_OBJECT
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            if ("messages".equals(tag))
            {
                jp.nextToken(); // START_ARRAY
                while (jp.nextToken() != JsonToken.END_ARRAY)
                {
                    HistoryMessage msg = parseHistoryMessage(origin, mapper, jp);

                    if (messages == null)
                    {
                        messages = new ArrayList<HistoryMessage>();
                    }

                    messages.add(msg);
                }
            }
        }

        if (messages == null)
        {
            messages = Collections.emptyList();
        }

        return messages;

    }

    private static HistoryMessage parseHistoryMessage(Room origin, ObjectMapper mapper, JsonParser jp) throws JsonParseException, IOException
    {
        HistoryMessage result = null;

        String dateString = null;
        String fromName = null;
        Object fromUserId = null;
        String message = null;
        String fileName = null;
        Integer fileSize = null;
        String fileUrl = null;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            jp.nextToken();
            if ("date".equals(tag))
            {
                dateString = UtilParser.parseString(jp);
            } else if ("message".equals(tag))
            {
                message = UtilParser.parseString(jp);
            } else if ("from".equals(tag))
            {
                while (jp.nextToken() != JsonToken.END_OBJECT)
                {
                    tag = jp.getText();
                    jp.nextToken();
                    if ("user_id".equals(tag))
                    {
                        fromUserId = UtilParser.parseString(jp);
                    } else if ("name".equals(tag))
                    {
                        fromName = UtilParser.parseString(jp);
                    } else
                    {
                        jp.skipChildren();
                    }
                }
            } else if ("file".equals(tag))
            {
                while (jp.nextToken() != JsonToken.END_OBJECT)
                {
                    tag = jp.getText();
                    jp.nextToken();
                    if ("size".equals(tag))
                    {
                        fileSize = jp.getIntValue();
                    } else if ("name".equals(tag))
                    {
                        fileName = UtilParser.parseString(jp);
                    } else if ("url".equals(tag))
                    {
                        fileUrl = UtilParser.parseString(jp);
                    } else
                    {
                        jp.skipChildren();
                    }
                }
            }

        }

        UserId from = UserId.create(fromUserId.toString(), fromName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = null;
        try
        {
            date = sdf.parse(dateString);
        } catch (ParseException e)
        {
            // does not fail
            e.printStackTrace();
        }

        UploadReference ref = null;
        if (fileName != null && fileName.length() > 0)
        {
            UploadReference.create(fileName, fileSize, fileUrl);
        }

        result = HistoryMessage.create(origin, from, message, date, ref);

        return result;
    }

}
