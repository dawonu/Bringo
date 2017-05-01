package com.example.bringo;

/**
 * Created by huojing on 4/30/17.
 */

public class CreateDestination2HashClass {
    private int itemID;
    private int categoryID;
    private boolean checked;

    public CreateDestination2HashClass(int itemID, int categoryID, boolean checked){
        this.itemID = itemID;
        this.categoryID = categoryID;
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
