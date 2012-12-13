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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;

import com.github.hipchat.api.messages.HistoryMessage;
import com.github.hipchat.api.messages.Message.Color;
import com.github.hipchat.api.messages.MessageParser;

public class RoomId implements Comparable<RoomId>
{
    private String id;
    private HipChat origin;

    public static RoomId roomId(String id, HipChat origin)
    {
        return new RoomId(id, origin);
    }

    protected RoomId(String id, HipChat origin)
    {
        this.id = id;
        this.origin = origin;
    }

    protected HipChat getOrigin()
    {
        return origin;
    }

    public String getId()
    {
        return id;
    }

    public int compareTo(RoomId o)
    {
        return this.id.compareTo(o.id);
    }

    public boolean sendMessage(String message, UserId from, boolean notify, Color color)
    {  
        String query = String.format(HipChatConstants.ROOMS_MESSAGE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, getOrigin().getAuthToken());

        StringBuilder params = new StringBuilder();

        if (message == null || from == null)
        {  
            throw new IllegalArgumentException("Cant send message with null message or null user");
        } else
        {  
            params.append("room_id=");
            params.append(getId());
            params.append("&from=");
            try
            {  
                params.append(URLEncoder.encode(from.getName(), "UTF-8"));
                params.append("&message=");
                params.append(URLEncoder.encode(message, "UTF-8"));
            } catch (UnsupportedEncodingException e)
            {  
                throw new RuntimeException(e);
            }

        }

        if (notify)
        {  
            params.append("&notify=1");
        }

        if (color != null)
        {  
            params.append("&color=");
            params.append(color.name().toLowerCase());
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;

        HttpURLConnection connection = null;
        boolean result = false;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_MESSAGE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = UtilParser.parseMessageResult(input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(output);
            connection.disconnect();
        }

        return result;
    }

}
