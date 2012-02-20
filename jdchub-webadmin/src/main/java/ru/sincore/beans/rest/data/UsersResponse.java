package ru.sincore.beans.rest.data;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User response data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 17.02.12
 *         Time: 11:40
 */
@XmlRootElement(name = "users_response")
@XmlAccessorType(XmlAccessType.NONE)
public class UsersResponse extends CommonResponse
{
    @XmlElement(name =  "users")
    private List<UserItem> users = null;


    public List<UserItem> getUsers()
    {
        return users;
    }


    public void setUsers(List<UserItem> users)
    {
        this.users = users;
    }
}
