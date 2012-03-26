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

public interface HipChatConstants
{
    public static final String JSON_FORMAT = "json";
    public static final String API_BASE = "https://api.hipchat.com/v1/";

    // API Operation URL components
    public static final String ROOMS_LIST = "rooms/list";
    public static final String ROOMS_LIST_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String ROOMS_CREATE = "rooms/create";
    public static final String ROOMS_CREATE_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String ROOMS_DELETE = "rooms/delete";
    public static final String ROOMS_DELETE_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String ROOMS_SHOW = "rooms/show";
    public static final String ROOMS_SHOW_QUERY_FORMAT = "?room_id=%s&format=%s&auth_token=%s";

    public static final String ROOMS_HISTORY = "rooms/history";
    public static final String ROOMS_HISTORY_QUERY_FORMAT = "?room_id=%s&date=%s&timezone=%s&format=%s&auth_token=%s";

    public static final String ROOMS_MESSAGE = "rooms/message";
    public static final String ROOMS_MESSAGE_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String USERS_CREATE = "users/create";
    public static final String USERS_CREATE_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String USERS_DELETE = "users/delete";
    public static final String USERS_DELETE_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String USERS_LIST = "users/list";
    public static final String USERS_LIST_QUERY_FORMAT = "?format=%s&auth_token=%s";

    public static final String USERS_SHOW = "users/show";
    public static final String USERS_SHOW_QUERY_FORMAT = "?user_id=%s&format=%s&auth_token=%s";

    public static final String USERS_UPDATE = "users/update";
    public static final String USERS_UPDATE_QUERY_FORMAT = "?format=%s&auth_token=%s";
}
