package de.kreth.clubhelperbackend.pojo;

import java.util.Date;

public interface Data {

    public Long getId() ;

    public void setId(Long id);

    public Date getChanged();

    public void setChanged(Date changed);

    public Date getCreated();

    public void setCreated(Date created);


}
