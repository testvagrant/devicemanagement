package com.testvagrant.devicemanagement.core;

public interface Constants {

    String DATABASE_NAME = "optimus";
    String ID = "_id";

    String COLLECTION_DEVICES = "devices";
    String KEY_DEVICES_DEVICENAME = "deviceName";
    String KEY_DEVICES_UDID = "udid";
    String KEY_DEVICES_STATUS = "status";
    String KEY_DEVICES_PLATFORM = "platform";
    String KEY_DEVICES_PLATFORM_VERSION = "platformVersion";
    String KEY_DEVICES_RUNSON = "runsOn";

    String COLLECTION_BUILDS = "builds";
    String KEY_BUILD_ID = "buildId";
    String KEY_BUILDS_BUILD_START_TIME = "buildStartTime";
    String KEY_BUILDS_BUILD_END_TIME = "buildEndTime";
    String KEY_BUILDS_SCENARIOS_COUNT = "scenariosCount";
    String KEY_BUILDS_SCENARIO_SUCCESS_RATE = "scenarioSuccessRate";

    String COLLECTIONS_EXEC_DETAILS = "executionDetails";
    String KEY_EXEC_DETAILS_SCENARIO_ID = "scenarioId";
    String KEY_EXEC_DETAILS_STEPS = "steps";
    String KEY_EXEC_DETAILS_FAILED_SCREEN = "failedOnScreen";

    String COLLECTION_CRASHES = "crashes";
    String KEY_CRASHES_SCENARIO = "scenario";
    String KEY_CRASHES_STACKTRACE = "stacktrace";
    String KEY_CRASHES_ACTIVITY = "activity";

    String COLLECTION_CPU_STATS = "cpuStats";
    String KEY_CPU_STATS_TEST_RUN_ID = "testrunId";
    String KEY_CPU_STATS_INTERVAL = "interval";
    String KEY_CPU_STATS_USER_PERCENTAGE = "userPercentage";
    String KEY_CPU_STATS_KERNEL_PERCENTAGE = "kernelPercentage";
    String KEY_CPU_STATS_ACTIVITY = "activity";

    String COLLECTION_MEMORY_STATS = "memoryStats";
    String KEY_MEM_STATS_TEST_RUN_ID = "testrunId";
    String KEY_MEM_STATS_INTERVAL = "interval";
    String KEY_MEM_STATS_TOTAL = "total";
    String KEY_MEM_STATS_ACTUAL = "actual";
    String KEY_MEM_STATS_ACTIVITY = "activity";

    String COLLECTION_SCENARIOS = "scenarios";
    String KEY_SCENARIO_ID = "scenarioID";
    String KEY_SCENARIOS_SCENARIO_NAME = "scenarioName";
    String KEY_SCENARIOS_DEVICE_UDID = "deviceUdid";
    String KEY_SCENARIOS_TAGS = "tags";
    String KEY_SCENARIOS_START_TIME = "startTime";
    String KEY_SCENARIOS_STATUS = "status";
    String KEY_SCENARIOS_BUILD_TIME = "buildTime";
    String KEY_SCENARIOS_COMPLETED = "completed";
    String KEY_SCENARIOS_END_TIME = "endTime";
    String KEY_SCENARIOS_TIME_TAKEN = "timeTaken";
    String KEY_SCENARIOS_TIMELINE ="scenarioTimeline";

    String COLLECTION_SCREENSHOTS = "screenshots";
    String KEY_SCREENSHOTS_TEST_RUN_ID = "testrunId";
    String KEY_SCREENSHOTS_INTERVAL = "interval";
    String KEY_SCREENSHOTS_SCREENSHOT = "screenshot";
    String KEY_SCREENSHOTS_ACTIVITY = "activity";
    String KEY_SCREENSHOTS_FILE ="file";


    String QUERY_AND = "$and";
    String QUERY_SET = "$set";
    String QUERY_NE = "$ne";
    String QUERY_EXISTS="$exists";

    String STATUS_AVAILABLE ="Available";
    String STATUS_ENGAGED="Engaged";
    String STATUS_PASSED="passed";

    String COLLECTION_INTELLISENSE = "intellisense";
    String KEY_EXCEPTIONSENSE = "exceptionsense";

}
