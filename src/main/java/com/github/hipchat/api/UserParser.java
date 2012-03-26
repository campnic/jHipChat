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

public class UserParser
{
    public static List<User> parseUserList(HipChat origin, InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();

        List<User> users = null;

        jp.nextToken(); // START_OBJECT
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            if ("users".equals(tag))
            {
                jp.nextToken(); // START_ARRAY
                while (jp.nextToken() != JsonToken.END_ARRAY)
                {
                    User user = parseUser(origin, mapper, jp);

                    if (users == null)
                    {
                        users = new ArrayList<User>();
                    }

                    users.add(user);
                }
            }
        }

        if (users == null)
        {
            users = Collections.emptyList();
        }

        return users;
    }

    public static User parseUser(HipChat origin, InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();
        User user = null;

        jp.nextToken(); // START_OBJECT
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            if ("user".equals(tag))
            {
                jp.nextToken(); // START_OBJECT
                user = parseUser(origin, mapper, jp);
            }
        }

        return user;
    }

    private static User parseUser(HipChat origin, ObjectMapper mapper, JsonParser jp) throws JsonParseException, JsonMappingException, IOException
    {
        User result = null;

        String userId = null;
        String name = null;
        String email = null;
        String title = null;
        String password = null;
        String photoUrl = null;
        String status = null;
        String statusMessage = null;
        boolean isGroupAdmin = false;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String tag = jp.getText();
            jp.nextToken();
            if ("user_id".equals(tag))
            {
                userId = UtilParser.parseString(jp);
            } else if ("name".equals(tag))
            {
                name = UtilParser.parseString(jp);
            } else if ("email".equals(tag))
            {
                email = UtilParser.parseString(jp);
            } else if ("title".equals(tag))
            {
                title = UtilParser.parseString(jp);
            } else if ("is_group_admin".equals(tag))
            {
                int admin = jp.getIntValue();
                isGroupAdmin = (admin == 1);
            } else if ("password".equals(tag))
            {
                password = UtilParser.parseString(jp);
            } else if ("photo_url".equals(tag))
            {
                photoUrl = UtilParser.parseString(jp);
            } else if ("status".equals(tag))
            {
                status = UtilParser.parseString(jp);
            } else if ("status_message".equals(tag))
            {
                statusMessage = UtilParser.parseString(jp);
            }
        }

        result = User.create(userId, name, email, title, photoUrl, password, status, statusMessage, isGroupAdmin);

        return result;
    }
}
