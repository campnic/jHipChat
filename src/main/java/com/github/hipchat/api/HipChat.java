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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;

public class HipChat
{

    public static JsonFactory JSON_FACTORY = new JsonFactory();

    private String authToken = null;

    public HipChat(String authToken)
    {
        this.authToken = authToken;
    }

    public String getAuthToken()
    {
        return this.authToken;
    }

    public List<Room> listRooms()
    {
        String query = String.format(HipChatConstants.ROOMS_LIST_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);

        InputStream input = null;
        List<Room> results = null;
        HttpURLConnection connection = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_LIST + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoInput(true);
            input = connection.getInputStream();
            results = RoomParser.parseRoomList(this, input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return results;
    }

    public Room createRoom(String name, String ownerId, boolean isPrivate, String topic, boolean allowGuests) throws IOException
    {
        String query = String.format(HipChatConstants.ROOMS_CREATE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);

        // build param string
        StringBuilder params = new StringBuilder();
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("Cannot create room with null or empty name");
        } else
        {
            params.append("name=");
            params.append(name);
        }

        if (ownerId != null && ownerId.length() != 0)
        {
            params.append("&owner_user_id=");
            params.append(ownerId);
        }

        if (isPrivate)
        {
            params.append("&privacy=private");
        } else
        {
            params.append("&privacy=public");
        }

        if (topic != null && topic.length() != 0)
        {
            params.append("&topic=");
            params.append(topic);
        }

        if (allowGuests)
        {
            params.append("&guest_access=1");
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;
        Room result = null;
        HttpURLConnection connection = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_CREATE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = RoomParser.parseRoom(this, input);

        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public boolean deleteRoom(String room_id)
    {
        String query = String.format(HipChatConstants.ROOMS_DELETE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);
        StringBuilder params = new StringBuilder();

        if (room_id == null || room_id.length() == 0)
        {
            throw new IllegalArgumentException("Cannot delete room with null or empty id");
        } else
        {
            params.append("room_id=");
            params.append(room_id);
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;

        HttpURLConnection connection = null;
        boolean result = false;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_DELETE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = UtilParser.parseDeleteResult(input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public Room getRoom(String roomId)
    {
        String query = String.format(HipChatConstants.ROOMS_SHOW_QUERY_FORMAT, roomId, HipChatConstants.JSON_FORMAT, authToken);

        InputStream input = null;
        Room result = null;
        HttpURLConnection connection = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.ROOMS_SHOW + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoInput(true);
            input = connection.getInputStream();
            result = RoomParser.parseRoom(this, input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public User createUser(String email, String name, String title, boolean isGroupAdmin, String password, String timeZoneId)
    {
        TimeZone tz = TimeZone.getTimeZone(timeZoneId);
        return createUser(email, name, title, isGroupAdmin, password, tz);
    }

    public User createUser(String email, String name, String title, boolean isGroupAdmin, String password, TimeZone timeZone)
    {
        String query = String.format(HipChatConstants.USERS_CREATE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);
        StringBuilder params = new StringBuilder();

        if (email == null || "".equals(email))
        {
            throw new IllegalArgumentException("createUser: email cannot be null or empty");
        } else
        {
            params.append("email=");
            params.append(email);
        }

        if (name == null || "".equals(name))
        {
            throw new IllegalArgumentException("createUser: name cannot be null or empty");
        } else if (!name.contains(" "))
        {
            throw new IllegalArgumentException("createUser: name must contain a space separating first and last name");
        } else
        {
            params.append("&name=");
            params.append(name);
        }

        if (title == null || "".equals(title))
        {
            throw new IllegalArgumentException("createUser: title cannot be null or empty");
        } else
        {
            params.append("&title=");
            params.append(title);
        }

        if (isGroupAdmin)
        {
            params.append("&is_group_admin=1");
        } else
        {
            params.append("&is_group_admin=0");
        }

        if (password != null && !"".equals(password))
        {
            params.append("&password=");
            params.append(password);
        }

        if (timeZone != null)
        {
            String tz = timeZone.getID();
            params.append("&timezone=");
            params.append(tz);
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;

        HttpURLConnection connection = null;
        User result = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.USERS_CREATE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = UserParser.parseUser(this, input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public void updateUser(User user)
    {
        updateUser(user, user.getEmail(), user.getName(), user.getTitle(), user.isGroupAdmin, user.password, null);
    }

    public User updateUser(UserId id, String email, String name, String title, boolean isGroupAdmin, String password, TimeZone timeZone)
    {
        String query = String.format(HipChatConstants.USERS_CREATE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);
        StringBuilder params = new StringBuilder();

        if (id == null)
        {
            throw new IllegalArgumentException("updateUser: id cannot be null");
        } else
        {
            params.append("user_id=");
            params.append(id.getId());
        }

        if (email != null)
        {
            params.append("&email=");
            params.append(email);
        }

        if (name != null)
        {
            if (!name.contains(" "))
            {
                throw new IllegalArgumentException("updateUser: name must contain a space separating first and last name");
            } else
            {
                params.append("&name=");
                params.append(name);
            }
        }

        if (title != null)
        {
            params.append("&title=");
            params.append(title);
        }

        if (isGroupAdmin)
        {
            params.append("&is_group_admin=1");
        } else
        {
            params.append("&is_group_admin=0");
        }

        if (password != null)
        {
            params.append("&password=");
            params.append(password);
        }

        if (timeZone != null)
        {
            String tz = timeZone.getID();
            params.append("&timezone=");
            params.append(tz);
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;

        HttpURLConnection connection = null;
        User result = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.USERS_UPDATE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = UserParser.parseUser(this, input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public boolean deleteUser(UserId id)
    {
        String query = String.format(HipChatConstants.USERS_DELETE_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);
        StringBuilder params = new StringBuilder();

        if (id == null)
        {
            throw new IllegalArgumentException("Cannot delete user with null or empty id");
        } else
        {
            params.append("user_id=");
            params.append(id.getId());
        }

        final String paramsToSend = params.toString();

        OutputStream output = null;
        InputStream input = null;

        HttpURLConnection connection = null;
        boolean result = false;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.USERS_DELETE + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(paramsToSend.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            output = new BufferedOutputStream(connection.getOutputStream());
            IOUtils.write(paramsToSend, output);
            IOUtils.closeQuietly(output);

            input = connection.getInputStream();
            result = UtilParser.parseDeleteResult(input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

    public List<User> listUsers()
    {
        String query = String.format(HipChatConstants.USERS_LIST_QUERY_FORMAT, HipChatConstants.JSON_FORMAT, authToken);

        InputStream input = null;
        List<User> results = null;
        HttpURLConnection connection = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.USERS_LIST + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoInput(true);
            input = connection.getInputStream();
            results = UserParser.parseUserList(this, input);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return results;
    }

    public User getUser(UserId id)
    {
        String query = String.format(HipChatConstants.USERS_SHOW_QUERY_FORMAT, id.getId(), HipChatConstants.JSON_FORMAT, authToken);

        InputStream input = null;
        User result = null;
        HttpURLConnection connection = null;

        try
        {
            URL requestUrl = new URL(HipChatConstants.API_BASE + HipChatConstants.USERS_SHOW + query);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setDoInput(true);
            input = connection.getInputStream();
            result = UserParser.parseUser(this, input);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            IOUtils.closeQuietly(input);
            connection.disconnect();
        }

        return result;
    }

}
