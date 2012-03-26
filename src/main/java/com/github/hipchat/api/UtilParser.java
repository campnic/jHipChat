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
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class UtilParser
{
    private static final String DELETED_RESULT_TOKEN = "deleted";
    private static final String STATUS_RESULT_TOKEN = "status";
    private static final String STATUS_SENT_VALUE = "sent";

    public static boolean parseDeleteResult(InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> data = mapper.readValue(jp, new TypeReference<Map<String, Object>>()
        {
        });

        boolean result = (Boolean) data.get(DELETED_RESULT_TOKEN);

        return result;

    }

    public static boolean parseMessageResult(InputStream input) throws JsonParseException, JsonMappingException, IOException
    {
        JsonParser jp = HipChat.JSON_FACTORY.createJsonParser(input);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> data = mapper.readValue(jp, new TypeReference<Map<String, Object>>()
        {
        });

        String result = (String) data.get(STATUS_RESULT_TOKEN);

        return STATUS_SENT_VALUE.equals(result);

    }

    public static String parseString(JsonParser jp) throws JsonParseException, IOException
    {
        String result = null;
        if (!jp.getCurrentToken().equals(JsonToken.VALUE_NULL))
        {
            result = jp.getText();
        }

        return result;
    }
}
