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

public class Room extends RoomId
{
    private String name;
    private String topic;
    private Date lastActive;
    private Date created;
    private String ownerId;
    private Boolean isArchived;
    private Boolean isPrivate;
    private String xmppJId;
    private String guestAccessUrl;
    private List<UserId> participants;

    private Room(String id, HipChat origin)
    {
        super(id, origin);
    }

    public String getName()
    {
        return name;
    }

    public String getTopic()
    {
        return topic;
    }

    public Date getLastActive()
    {
        return lastActive;
    }

    public Date getCreated()
    {
        return created;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public Boolean getIsArchived()
    {
        return isArchived;
    }

    public Boolean getIsPrivate()
    {
        return isPrivate;
    }

    public String getXmppJId()
    {
        return xmppJId;
    }

    public List<UserId> getParticipants()
    {
        return participants;
    }

    public String getGuestAccessUrl()
    {
        return guestAccessUrl;
    }

    public static Room create(String id, HipChat origin)
    {
        return new Room(id, origin);
    }

    public static Room create(String id, HipChat origin, String name, String topic, long lastActive, long created, String ownerId, Boolean isArchived,
            Boolean isPrivate, String xmppJId, List<UserId> participants, String guestAccessUrl)
    {
        Room room = new Room(id, origin);
        room.name = name;
        room.topic = topic;
        room.lastActive = new Date(lastActive);
        room.created = new Date(created);
        room.ownerId = ownerId;
        room.isArchived = isArchived;
        room.isPrivate = isPrivate;
        room.xmppJId = xmppJId;
        room.participants = participants;
        room.guestAccessUrl = guestAccessUrl;
        return room;
    }

    public List<HistoryMessage> getHistory()
    {
        return getHistory(null);
    }

    public List<HistoryMessage> getHistory(Date date)
    {
        HipChat hc = getOrigin();
        Calendar c = Calendar.getInstance();
        String dateString = null;
        String tzString = null;
        if (date != null)
        {
            c.setTime(date);
            TimeZone tz = c.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateString = sdf.format(date);
            tzString = tz.getDisplayName(tz.inDaylightTime(date), TimeZone.SHORT);
        } else
        {
            Date tDate = new Date();
            c.setTime(tDate);
            TimeZone tz = c.getTimeZone();
            dateString = "recent";
            tzString = tz.getDisplayName(tz.inDaylightTime(tDate), TimeZone.SHORT);
        }

        String query = String.format(HipChatConstants.ROOMS_HISTORY_QUERY_FORMAT, getId(), dateString, tzString, HipChatConstants.JSON_FORMAT,
                hc.getAuthToken());

        OutputStream output = null;
        InputStream input = null;
        HttpURLConnection connection = null;

        List<HistoryMessage> messages = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_HISTORY + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoInput(true);
            input = connection.getInputStream();

            messages = MessageParser.parseRoomHistory(this, input);

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

        return messages;
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
