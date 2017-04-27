package com.example.bringo;

/**
 * Created by xuyidi on 4/25/17.
 */

public class CreateSceTwoHashClass {
    private int itemID;
    private boolean checked;

    public CreateSceTwoHashClass(int itemID, boolean checked){
        this.itemID = itemID;
        this.checked = checked;
    }

    public int getItemID(){
        return itemID;
    }

    public boolean getCheckedStatus(){
        return checked;
    }

    public void setCheckedStatus(boolean status){
        this.checked = status;
    }


}
