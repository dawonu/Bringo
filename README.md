# Project
An Android Application bringo

BringoServer->dispatcher.java is the server side code

LoginActivity.java, RegisterActivity.java handles login and register.

BringoTracker->BringoTracker.ino is the code in the Arduino Uno board

HomeActivity.java, GetScenarios.java are responsible for the home page.

DefaultListActivity.java, GetItemList.java are responsible for list pages of all default scenarios.

CreateSceActivity.java, CreateSceTwoActivity.java, CreateSceTwoHashClass.java are responsible for creating customized scenarios.

SetAlarmActivity.java and EditAlarmActivity.java are responsible for setting notification alarms for every scenario.

NotificationReceiver.java is used to create notifications on mobile.

TodayListActivity.java, WeatherAPI.java (in supportingapis package) are responsible for showing  Today’s list with weather items.

TrackActivity.java, TrackAddActivity.java, TrackEdit1Activity.java, TrackEdit2Activity.java, TrackEdit3Activity.java is responsible for tracker functionalities. 

Setting.java provides options for customized settings.

CalendarActivity.java handles the import of google Calendar.

TravelActivity.java is the main page for travel, which is different from other default scenarios.
CreateDestination1.java is the first step of creating a destination. PlaneFragment.java, TrainFragment.java and CarFragment.java are sliding windows in it.

ViewDestination.java shows the saved destinations.

CreateDestination2.java is the second step of creating a destination. 

CreateDestination2HashClass.java records the checked information of every item.

All java files in database package are Sugar ORM classes for interaction with local SQLite database.


