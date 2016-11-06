package com.example.admin.mylocation;

/**
 * Created by admin on 2016/10/29.
 */
public class Message {
    private  Integer  _what;
    private  Object _obj;
    public Integer what ()
    {
        return _what;
    }
    public void  what(Integer _what)
    {
        this._what=_what;
    }
    public Object obj()
    {
        return _obj;
    }
    public void     obj(String _obj)
    {
        this._obj=_obj;
    }
}
