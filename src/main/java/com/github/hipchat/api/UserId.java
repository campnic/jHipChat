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

public class UserId implements Serializable, Comparable<UserId>
{

    /**
     * 
     */
    private static final long serialVersionUID = 4723547653495337132L;

    protected String id;
    protected String name;

    protected UserId(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public static UserId create(String id, String name)
    {
        UserId user = new UserId(id, name);
        return user;
    }

    public static UserId create(String id)
    {
        UserId user = new UserId(id, null);
        return user;
    }

    public int compareTo(UserId o)
    {
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof UserId)
        {
            return this.id.equals(((UserId) obj).getId());
        }

        return false;
    }
}
