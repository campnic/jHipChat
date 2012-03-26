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

import java.io.Serializable;

public class User extends UserId implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -5544013930790731593L;

    protected String email;
    protected String title;
    protected String photoUrl;
    protected String status;
    protected String statusMessage;
    protected Boolean isGroupAdmin;
    protected String password;

    public String getEmail()
    {
        return email;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPhotoUrl()
    {
        return photoUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public Boolean isGroupAdmin()
    {
        return isGroupAdmin;
    }

    public String getPassword()
    {
        return password;
    }

    private User(String id, String name)
    {
        super(id, name);
    }

    //
    // static User create(String id, String name)
    // {
    // return new User(id, name);
    // }

    static User create(String id, String name, String email, String title, String photoUrl, String password, String status, String statusMessage,
            Boolean isGroupAdmin)
    {
        User user = new User(id, name);
        user.email = email;
        user.title = title;
        user.photoUrl = photoUrl;
        user.status = status;
        user.statusMessage = statusMessage;
        user.isGroupAdmin = isGroupAdmin;
        user.password = password;
        return user;
    }
}
