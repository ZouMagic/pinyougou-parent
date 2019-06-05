package entity;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbUser;

import java.io.Serializable;

public class TbInfo  implements Serializable{
    private TbAddress address;

    private TbUser user;

    public TbAddress getAddress() {
        return address;
    }

    public void setAddress(TbAddress address) {
        this.address = address;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }
}
